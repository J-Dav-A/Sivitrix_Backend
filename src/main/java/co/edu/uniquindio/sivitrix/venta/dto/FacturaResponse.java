package co.edu.uniquindio.sivitrix.venta.dto;

import co.edu.uniquindio.sivitrix.venta.entity.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FacturaResponse(
        Long id,
        String numero,
        Long ventaId,
        LocalDateTime fechaEmision,
        TipoPago tipoPago,
        Cliente cliente,
        List<Linea> lineas,
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal total,
        Boolean anulada
) {
    public record Cliente(String nit, String nombre, String direccion, String telefono, String email) {
    }

    public record Linea(
            String productoCodigo,
            String productoNombre,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal
    ) {
    }
}
