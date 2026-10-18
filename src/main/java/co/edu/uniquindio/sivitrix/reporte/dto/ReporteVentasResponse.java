package co.edu.uniquindio.sivitrix.reporte.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReporteVentasResponse(
        LocalDate desde,
        LocalDate hasta,
        int cantidadVentas,
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal total,
        BigDecimal costo,
        BigDecimal ganancia,
        List<DiaVentas> porDia
) {
    public record DiaVentas(LocalDate fecha, int cantidadVentas, BigDecimal total) {
    }
}
