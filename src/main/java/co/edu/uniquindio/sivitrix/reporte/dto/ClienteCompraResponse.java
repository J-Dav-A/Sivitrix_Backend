package co.edu.uniquindio.sivitrix.reporte.dto;

import java.math.BigDecimal;

public record ClienteCompraResponse(
        Long clienteId,
        String nit,
        String nombre,
        int compras,
        BigDecimal total
) {
}
