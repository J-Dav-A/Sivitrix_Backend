package co.edu.uniquindio.sivitrix.pedido.service;

import co.edu.uniquindio.sivitrix.common.exception.BusinessRuleException;
import co.edu.uniquindio.sivitrix.common.exception.ResourceNotFoundException;
import co.edu.uniquindio.sivitrix.inventario.service.InventarioService;
import co.edu.uniquindio.sivitrix.pedido.dto.PedidoRequest;
import co.edu.uniquindio.sivitrix.pedido.dto.PedidoResponse;
import co.edu.uniquindio.sivitrix.pedido.dto.RecepcionRequest;
import co.edu.uniquindio.sivitrix.pedido.entity.DetallePedido;
import co.edu.uniquindio.sivitrix.pedido.entity.EstadoPedido;
import co.edu.uniquindio.sivitrix.pedido.entity.Pedido;
import co.edu.uniquindio.sivitrix.pedido.repository.PedidoRepository;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import co.edu.uniquindio.sivitrix.producto.repository.ProductoRepository;
import co.edu.uniquindio.sivitrix.proveedor.entity.Proveedor;
import co.edu.uniquindio.sivitrix.proveedor.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    @Override
    public PedidoResponse crear(PedidoRequest request) {
        Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el proveedor con id " + request.proveedorId()));

        Pedido pedido = new Pedido();
        pedido.setProveedor(proveedor);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.REGISTRADO);
        pedido.setObservaciones(request.observaciones());

        for (PedidoRequest.Linea linea : request.detalles()) {
            Producto producto = productoRepository.findById(linea.productoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontro el producto con id " + linea.productoId()));
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidadSolicitada(linea.cantidad());
            detalle.setCantidadRecibida(0);
            pedido.getDetalles().add(detalle);
        }

        // SWR-34: el proveedor registra la fecha de su ultimo pedido.
        proveedor.setFechaUltimoPedido(pedido.getFechaPedido());
        proveedorRepository.save(proveedor);

        return toResponse(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(Long proveedorId) {
        List<Pedido> pedidos = (proveedorId != null)
                ? pedidoRepository.findByProveedorIdOrderByFechaPedidoDesc(proveedorId)
                : pedidoRepository.findAllByOrderByFechaPedidoDesc();
        return pedidos.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(Long id) {
        return toResponse(buscarOFallar(id));
    }

    @Override
    public PedidoResponse registrarRecepcion(Long id, RecepcionRequest request) {
        Pedido pedido = buscarOFallar(id);
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new BusinessRuleException("El pedido #" + id + " esta cancelado y no admite recepciones");
        }
        if (pedido.getEstado() == EstadoPedido.RECIBIDO) {
            throw new BusinessRuleException("El pedido #" + id + " ya fue recibido por completo");
        }

        for (RecepcionRequest.Linea linea : request.lineas()) {
            DetallePedido detalle = pedido.getDetalles().stream()
                    .filter(d -> d.getId().equals(linea.detalleId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "El detalle " + linea.detalleId() + " no pertenece al pedido #" + id));

            int pendiente = detalle.getCantidadSolicitada() - detalle.getCantidadRecibida();
            if (linea.cantidad() > pendiente) {
                throw new BusinessRuleException("Se intenta recibir " + linea.cantidad() + " unidades de '"
                        + detalle.getProducto().getNombre() + "' pero solo faltan " + pendiente);
            }

            detalle.setCantidadRecibida(detalle.getCantidadRecibida() + linea.cantidad());
            // F-02: la mercancia recibida entra al inventario y deja su movimiento.
            inventarioService.registrarEntrada(detalle.getProducto(), linea.cantidad(),
                    "Entrada por pedido #" + id, pedido);
        }

        boolean todoRecibido = pedido.getDetalles().stream()
                .allMatch(d -> d.getCantidadRecibida() >= d.getCantidadSolicitada());
        pedido.setEstado(todoRecibido ? EstadoPedido.RECIBIDO : EstadoPedido.RECIBIDO_PARCIAL);

        return toResponse(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoResponse cancelar(Long id) {
        Pedido pedido = buscarOFallar(id);
        if (pedido.getEstado() != EstadoPedido.REGISTRADO) {
            throw new BusinessRuleException(
                    "Solo se puede cancelar un pedido que aun no ha recibido mercancia (estado REGISTRADO)");
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        return toResponse(pedidoRepository.save(pedido));
    }

    private Pedido buscarOFallar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el pedido con id " + id));
    }

    private PedidoResponse toResponse(Pedido p) {
        List<PedidoResponse.Detalle> detalles = p.getDetalles().stream()
                .map(d -> new PedidoResponse.Detalle(
                        d.getId(),
                        d.getProducto().getId(),
                        d.getProducto().getCodigo(),
                        d.getProducto().getNombre(),
                        d.getCantidadSolicitada(),
                        d.getCantidadRecibida(),
                        d.getCantidadSolicitada() - d.getCantidadRecibida()))
                .toList();
        return new PedidoResponse(
                p.getId(),
                p.getProveedor().getId(),
                p.getProveedor().getNombreEmpresa(),
                p.getFechaPedido(),
                p.getEstado(),
                p.getObservaciones(),
                detalles);
    }
}
