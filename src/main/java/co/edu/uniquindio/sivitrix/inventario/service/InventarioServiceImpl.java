package co.edu.uniquindio.sivitrix.inventario.service;

import co.edu.uniquindio.sivitrix.alerta.service.AlertaService;
import co.edu.uniquindio.sivitrix.common.exception.BusinessRuleException;
import co.edu.uniquindio.sivitrix.common.exception.ResourceNotFoundException;
import co.edu.uniquindio.sivitrix.inventario.dto.MovimientoRequest;
import co.edu.uniquindio.sivitrix.inventario.dto.MovimientoResponse;
import co.edu.uniquindio.sivitrix.inventario.entity.MovimientoInventario;
import co.edu.uniquindio.sivitrix.inventario.entity.TipoMovimiento;
import co.edu.uniquindio.sivitrix.inventario.repository.MovimientoInventarioRepository;
import co.edu.uniquindio.sivitrix.pedido.entity.Pedido;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import co.edu.uniquindio.sivitrix.producto.repository.ProductoRepository;
import co.edu.uniquindio.sivitrix.venta.entity.Venta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventarioServiceImpl implements InventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final AlertaService alertaService;

    @Override
    public MovimientoResponse registrarMovimiento(MovimientoRequest request) {
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el producto con id " + request.productoId()));

        String obs = (request.observacion() == null || request.observacion().isBlank())
                ? "Ajuste manual" : request.observacion();

        MovimientoInventario mov = (request.tipo() == TipoMovimiento.ENTRADA)
                ? registrarEntrada(producto, request.cantidad(), obs, null)
                : registrarSalida(producto, request.cantidad(), obs, null);
        return toResponse(mov);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar(Long productoId) {
        List<MovimientoInventario> movimientos = (productoId != null)
                ? movimientoRepository.findByProductoIdOrderByFechaDesc(productoId)
                : movimientoRepository.findAllByOrderByFechaDesc();
        return movimientos.stream().map(this::toResponse).toList();
    }

    @Override
    public MovimientoInventario registrarEntrada(Producto producto, int cantidad, String observacion, Pedido pedido) {
        validarCantidad(cantidad);
        producto.setStockActual(producto.getStockActual() + cantidad);
        return guardar(producto, TipoMovimiento.ENTRADA, cantidad, observacion, pedido, null);
    }

    @Override
    public MovimientoInventario registrarSalida(Producto producto, int cantidad, String observacion, Venta venta) {
        validarCantidad(cantidad);
        // SWR-30: el stock nunca puede quedar negativo.
        if (producto.getStockActual() < cantidad) {
            throw new BusinessRuleException("Stock insuficiente para '" + producto.getNombre()
                    + "': disponible " + producto.getStockActual() + ", solicitado " + cantidad);
        }
        producto.setStockActual(producto.getStockActual() - cantidad);
        return guardar(producto, TipoMovimiento.SALIDA, cantidad, observacion, null, venta);
    }

    private MovimientoInventario guardar(Producto producto, TipoMovimiento tipo, int cantidad,
                                         String observacion, Pedido pedido, Venta venta) {
        productoRepository.save(producto);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setTipo(tipo);
        mov.setCantidad(cantidad);
        mov.setFecha(LocalDateTime.now());
        mov.setObservacion(observacion);
        mov.setPedido(pedido);
        mov.setVenta(venta);
        MovimientoInventario guardado = movimientoRepository.save(mov);

        // F-07: cada cambio de stock puede abrir o cerrar una alerta.
        alertaService.evaluar(producto);
        return guardado;
    }

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a cero");
        }
    }

    private MovimientoResponse toResponse(MovimientoInventario m) {
        Producto p = m.getProducto();
        return new MovimientoResponse(
                m.getId(),
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                m.getTipo(),
                m.getCantidad(),
                p.getStockActual(),
                m.getFecha(),
                m.getObservacion(),
                m.getPedido() != null ? m.getPedido().getId() : null,
                m.getVenta() != null ? m.getVenta().getId() : null
        );
    }
}
