package co.edu.uniquindio.sivitrix.inventario.dto;

import co.edu.uniquindio.sivitrix.inventario.entity.TipoMovimiento;

import java.time.LocalDateTime;

public record MovimientoResponse(
        Long id,
        Long productoId,
        String productoCodigo,
        String productoNombre,
        TipoMovimiento tipo,
        Integer cantidad,
        Integer stockResultante,
        LocalDateTime fecha,
        String observacion,
        Long pedidoId,
        Long ventaId
) {
}
