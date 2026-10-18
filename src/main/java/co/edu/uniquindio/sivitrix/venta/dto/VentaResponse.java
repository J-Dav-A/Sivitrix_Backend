package co.edu.uniquindio.sivitrix.venta.dto;

import co.edu.uniquindio.sivitrix.venta.entity.EstadoVenta;
import co.edu.uniquindio.sivitrix.venta.entity.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Long id,
        String numero,
        Long clienteId,
        String clienteNit,
        String clienteNombre,
        LocalDateTime fecha,
        TipoPago tipoPago,
        EstadoVenta estado,
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal total,
        Long facturaId,
        List<Detalle> detalles
) {
    public record Detalle(
            Long productoId,
            String productoCodigo,
            String productoNombre,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal
    ) {
    }
}
