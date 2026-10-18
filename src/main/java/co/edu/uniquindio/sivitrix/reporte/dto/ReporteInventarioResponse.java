package co.edu.uniquindio.sivitrix.reporte.dto;

import java.math.BigDecimal;

public record ReporteInventarioResponse(
        int totalProductos,
        long unidadesEnStock,
        BigDecimal valorAlCosto,
        BigDecimal valorAlPrecioVenta,
        BigDecimal gananciaPotencial,
        int productosBajoMinimo
) {
}
