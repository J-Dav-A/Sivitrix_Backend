package co.edu.uniquindio.sivitrix.reporte.dto;

import java.math.BigDecimal;

public record ProductoVendidoResponse(
        Long productoId,
        String codigo,
        String nombre,
        long unidades,
        BigDecimal ingresos,
        BigDecimal ganancia
) {
}
