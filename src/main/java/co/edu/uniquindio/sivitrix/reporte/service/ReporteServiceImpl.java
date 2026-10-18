package co.edu.uniquindio.sivitrix.reporte.service;

import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import co.edu.uniquindio.sivitrix.common.exception.BusinessRuleException;
import co.edu.uniquindio.sivitrix.common.util.MoneyUtils;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import co.edu.uniquindio.sivitrix.producto.repository.ProductoRepository;
import co.edu.uniquindio.sivitrix.reporte.dto.*;
import co.edu.uniquindio.sivitrix.venta.entity.DetalleVenta;
import co.edu.uniquindio.sivitrix.venta.entity.EstadoVenta;
import co.edu.uniquindio.sivitrix.venta.entity.Venta;
import co.edu.uniquindio.sivitrix.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * F-08: reportes. No tienen entidad propia: son consultas agregadas sobre
 * las ventas confirmadas y el inventario.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public ReporteVentasResponse ventas(LocalDate desde, LocalDate hasta) {
        List<Venta> ventas = ventasConfirmadas(desde, hasta);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal costo = BigDecimal.ZERO;
        for (Venta v : ventas) {
            subtotal = subtotal.add(v.getSubtotal());
            iva = iva.add(v.getIva());
            total = total.add(v.getTotal());
            for (DetalleVenta d : v.getDetalles()) {
                costo = costo.add(d.getCostoUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            }
        }

        // La ganancia es el subtotal (sin IVA, que se le debe al fisco) menos el costo de lo vendido.
        BigDecimal ganancia = subtotal.subtract(costo);

        Map<LocalDate, List<Venta>> porFecha = ventas.stream()
                .collect(Collectors.groupingBy(v -> v.getFecha().toLocalDate(), TreeMap::new, Collectors.toList()));
        List<ReporteVentasResponse.DiaVentas> porDia = porFecha.entrySet().stream()
                .map(e -> new ReporteVentasResponse.DiaVentas(
                        e.getKey(),
                        e.getValue().size(),
                        MoneyUtils.redondear(e.getValue().stream()
                                .map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add))))
                .toList();

        return new ReporteVentasResponse(desde, hasta, ventas.size(),
                MoneyUtils.redondear(subtotal), MoneyUtils.redondear(iva), MoneyUtils.redondear(total),
                MoneyUtils.redondear(costo), MoneyUtils.redondear(ganancia), porDia);
    }

    @Override
    public List<ProductoVendidoResponse> productosMasVendidos(LocalDate desde, LocalDate hasta, int limite) {
        Map<Long, long[]> unidades = new HashMap<>();
        Map<Long, BigDecimal[]> dinero = new HashMap<>();   // [ingresos, ganancia]
        Map<Long, Producto> productos = new HashMap<>();

        for (Venta v : ventasConfirmadas(desde, hasta)) {
            for (DetalleVenta d : v.getDetalles()) {
                Long id = d.getProducto().getId();
                productos.put(id, d.getProducto());
                unidades.computeIfAbsent(id, k -> new long[1])[0] += d.getCantidad();
                BigDecimal[] acc = dinero.computeIfAbsent(id, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
                acc[0] = acc[0].add(d.getSubtotal());
                BigDecimal costoLinea = d.getCostoUnitario().multiply(BigDecimal.valueOf(d.getCantidad()));
                acc[1] = acc[1].add(d.getSubtotal().subtract(costoLinea));
            }
        }

        return productos.values().stream()
                .map(p -> new ProductoVendidoResponse(
                        p.getId(), p.getCodigo(), p.getNombre(),
                        unidades.get(p.getId())[0],
                        MoneyUtils.redondear(dinero.get(p.getId())[0]),
                        MoneyUtils.redondear(dinero.get(p.getId())[1])))
                .sorted(Comparator.comparingLong(ProductoVendidoResponse::unidades).reversed())
                .limit(Math.max(1, limite))
                .toList();
    }

    @Override
    public List<ClienteCompraResponse> mejoresClientes(LocalDate desde, LocalDate hasta, int limite) {
        Map<Long, ClienteEmpresarial> clientes = new HashMap<>();
        Map<Long, Integer> compras = new HashMap<>();
        Map<Long, BigDecimal> totales = new HashMap<>();

        for (Venta v : ventasConfirmadas(desde, hasta)) {
            Long id = v.getCliente().getId();
            clientes.put(id, v.getCliente());
            compras.merge(id, 1, Integer::sum);
            totales.merge(id, v.getTotal(), BigDecimal::add);
        }

        return clientes.values().stream()
                .map(c -> new ClienteCompraResponse(
                        c.getId(), c.getNit(), c.getNombre(),
                        compras.get(c.getId()), MoneyUtils.redondear(totales.get(c.getId()))))
                .sorted(Comparator.comparing(ClienteCompraResponse::total).reversed())
                .limit(Math.max(1, limite))
                .toList();
    }

    @Override
    public ReporteInventarioResponse inventario() {
        List<Producto> productos = productoRepository.findAll();

        long unidades = 0;
        BigDecimal valorCosto = BigDecimal.ZERO;
        BigDecimal valorVenta = BigDecimal.ZERO;
        int bajoMinimo = 0;
        for (Producto p : productos) {
            BigDecimal stock = BigDecimal.valueOf(p.getStockActual());
            unidades += p.getStockActual();
            valorCosto = valorCosto.add(p.getCostoAdquisicion().multiply(stock));
            valorVenta = valorVenta.add(p.getPrecioVenta().multiply(stock));
            if (p.getStockActual() <= p.getStockMinimo()) {
                bajoMinimo++;
            }
        }
        return new ReporteInventarioResponse(
                productos.size(), unidades,
                MoneyUtils.redondear(valorCosto), MoneyUtils.redondear(valorVenta),
                MoneyUtils.redondear(valorVenta.subtract(valorCosto)), bajoMinimo);
    }

    private List<Venta> ventasConfirmadas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new BusinessRuleException("Indica el rango de fechas (desde y hasta)");
        }
        if (hasta.isBefore(desde)) {
            throw new BusinessRuleException("La fecha 'hasta' no puede ser anterior a 'desde'");
        }
        return ventaRepository.findByEstadoAndFechaBetween(
                EstadoVenta.CONFIRMADA, desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));
    }
}
