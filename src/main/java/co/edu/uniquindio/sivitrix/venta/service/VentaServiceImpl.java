package co.edu.uniquindio.sivitrix.venta.service;

import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import co.edu.uniquindio.sivitrix.cliente.repository.ClienteEmpresarialRepository;
import co.edu.uniquindio.sivitrix.common.exception.BusinessRuleException;
import co.edu.uniquindio.sivitrix.common.exception.ResourceNotFoundException;
import co.edu.uniquindio.sivitrix.common.util.FinanzasUtils;
import co.edu.uniquindio.sivitrix.common.util.MoneyUtils;
import co.edu.uniquindio.sivitrix.credito.entity.Credito;
import co.edu.uniquindio.sivitrix.credito.entity.EstadoCredito;
import co.edu.uniquindio.sivitrix.credito.repository.CreditoRepository;
import co.edu.uniquindio.sivitrix.inventario.service.InventarioService;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import co.edu.uniquindio.sivitrix.producto.repository.ProductoRepository;
import co.edu.uniquindio.sivitrix.venta.dto.VentaRequest;
import co.edu.uniquindio.sivitrix.venta.dto.VentaResponse;
import co.edu.uniquindio.sivitrix.venta.entity.*;
import co.edu.uniquindio.sivitrix.venta.repository.FacturaRepository;
import co.edu.uniquindio.sivitrix.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final FacturaRepository facturaRepository;
    private final ClienteEmpresarialRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CreditoRepository creditoRepository;
    private final InventarioService inventarioService;

    @Override
    public VentaResponse registrar(VentaRequest request) {
        ClienteEmpresarial cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el cliente empresarial con id " + request.clienteId()));

        if (request.tipoPago() == TipoPago.CREDITO && request.numeroCuotas() == null) {
            throw new BusinessRuleException("Una venta a credito requiere el numero de cuotas");
        }

        // Si el mismo producto llega en dos lineas, se suman para validar el stock real.
        Map<Long, Integer> cantidadesPorProducto = new LinkedHashMap<>();
        for (VentaRequest.Linea linea : request.detalles()) {
            cantidadesPorProducto.merge(linea.productoId(), linea.cantidad(), Integer::sum);
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now());
        venta.setTipoPago(request.tipoPago());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
            Producto producto = productoRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontro el producto con id " + entry.getKey()));

            int cantidad = entry.getValue();
            // SWR-30: no se vende mas de lo que hay en stock.
            if (producto.getStockActual() < cantidad) {
                throw new BusinessRuleException("Stock insuficiente para '" + producto.getNombre()
                        + "': disponible " + producto.getStockActual() + ", solicitado " + cantidad);
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecioVenta());
            detalle.setCostoUnitario(producto.getCostoAdquisicion());
            detalle.setSubtotal(MoneyUtils.redondear(
                    producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad))));
            venta.getDetalles().add(detalle);

            subtotal = subtotal.add(detalle.getSubtotal());
        }

        BigDecimal subtotalRedondeado = MoneyUtils.redondear(subtotal);
        BigDecimal iva = FinanzasUtils.calcularIva(subtotalRedondeado); // SWR-19
        venta.setSubtotal(subtotalRedondeado);
        venta.setIva(iva);
        venta.setTotal(FinanzasUtils.calcularTotal(subtotalRedondeado));
        venta.setNumero(String.format("VIT-%d-%05d", LocalDate.now().getYear(), ventaRepository.siguienteNumero()));

        if (request.tipoPago() == TipoPago.CONTADO) {
            venta.setEstado(EstadoVenta.CONFIRMADA);
            Venta guardada = ventaRepository.save(venta);
            descontarStockYFacturar(guardada);
            return toResponse(guardada);
        }

        // Credito: el cupo se valida ya, y queda pendiente de autorizacion del administrador.
        if (!cliente.puedeFinanciar(venta.getTotal())) {
            throw new BusinessRuleException("El total de la venta (" + venta.getTotal()
                    + ") excede el cupo de credito disponible del cliente (" + cliente.cupoDisponible() + ")");
        }

        venta.setEstado(EstadoVenta.PENDIENTE_AUTORIZACION);
        Venta guardada = ventaRepository.save(venta);

        Credito credito = new Credito();
        credito.setVenta(guardada);
        credito.setCliente(cliente);
        credito.setValorTotal(guardada.getTotal());
        credito.setNumeroCuotas(request.numeroCuotas());
        credito.setEstado(EstadoCredito.PENDIENTE_AUTORIZACION);
        credito.setFechaSolicitud(LocalDateTime.now());
        creditoRepository.save(credito);

        return toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> listar(EstadoVenta estado, Long clienteId) {
        return ventaRepository.findAllByOrderByFechaDesc().stream()
                .filter(v -> estado == null || v.getEstado() == estado)
                .filter(v -> clienteId == null || v.getCliente().getId().equals(clienteId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse obtenerPorId(Long id) {
        return toResponse(buscarOFallar(id));
    }

    @Override
    public void confirmarTrasAutorizacion(Long ventaId) {
        Venta venta = buscarOFallar(ventaId);
        venta.setEstado(EstadoVenta.CONFIRMADA);
        ventaRepository.save(venta);
        descontarStockYFacturar(venta);
    }

    @Override
    public void rechazarTrasAutorizacion(Long ventaId) {
        Venta venta = buscarOFallar(ventaId);
        venta.setEstado(EstadoVenta.RECHAZADA);
        ventaRepository.save(venta);
    }

    @Override
    public VentaResponse cancelar(Long id, String motivo) {
        Venta venta = buscarOFallar(id);
        if (venta.getEstado() != EstadoVenta.CONFIRMADA) {
            throw new BusinessRuleException("Solo se pueden cancelar ventas confirmadas (estado actual: "
                    + venta.getEstado() + ")");
        }

        if (venta.getTipoPago() == TipoPago.CREDITO) {
            Credito credito = creditoRepository.findByVentaId(venta.getId()).orElse(null);
            if (credito != null) {
                boolean hayPagos = credito.getCuotas().stream()
                        .anyMatch(c -> c.getEstado() == co.edu.uniquindio.sivitrix.credito.entity.EstadoCuota.PAGADA);
                if (hayPagos) {
                    throw new BusinessRuleException("No se puede cancelar: el credito ya tiene cuotas pagadas");
                }
                ClienteEmpresarial cliente = credito.getCliente();
                cliente.reducirSaldo(credito.getValorTotal());
                clienteRepository.save(cliente);

                credito.setEstado(EstadoCredito.CANCELADO);
                credito.setObservacion("Venta cancelada");
                creditoRepository.save(credito);
            }
        }

        String detalleMotivo = (motivo == null || motivo.isBlank()) ? "" : ": " + motivo;
        for (DetalleVenta d : venta.getDetalles()) {
            inventarioService.registrarEntrada(d.getProducto(), d.getCantidad(),
                    "Cancelacion de la venta " + venta.getNumero() + detalleMotivo, null);
        }

        venta.setEstado(EstadoVenta.CANCELADA);
        return toResponse(ventaRepository.save(venta));
    }

    // F-01 + F-06: al confirmarse la venta se descuenta el stock y se emite la factura.
    private void descontarStockYFacturar(Venta venta) {
        for (DetalleVenta d : venta.getDetalles()) {
            inventarioService.registrarSalida(d.getProducto(), d.getCantidad(), "Venta " + venta.getNumero(), venta);
        }

        Factura factura = new Factura();
        factura.setNumero(venta.getNumero());
        factura.setVenta(venta);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotal(venta.getSubtotal());
        factura.setIva(venta.getIva());
        factura.setTotal(venta.getTotal());
        facturaRepository.save(factura);
    }

    private Venta buscarOFallar(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la venta con id " + id));
    }

    private VentaResponse toResponse(Venta v) {
        List<VentaResponse.Detalle> detalles = v.getDetalles().stream()
                .map(d -> new VentaResponse.Detalle(
                        d.getProducto().getId(),
                        d.getProducto().getCodigo(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()))
                .toList();
        Long facturaId = facturaRepository.findByVentaId(v.getId()).map(Factura::getId).orElse(null);
        return new VentaResponse(
                v.getId(), v.getNumero(),
                v.getCliente().getId(), v.getCliente().getNit(), v.getCliente().getNombre(),
                v.getFecha(), v.getTipoPago(), v.getEstado(),
                v.getSubtotal(), v.getIva(), v.getTotal(),
                facturaId, detalles);
    }
}