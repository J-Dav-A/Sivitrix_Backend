package co.edu.uniquindio.sivitrix.inventario.dto;

import co.edu.uniquindio.sivitrix.inventario.entity.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Movimiento manual (ajuste, devolucion, merma). Las ventas y pedidos generan los suyos solos. */
public record MovimientoRequest(

        @NotNull(message = "el producto es obligatorio")
        Long productoId,

        @NotNull(message = "el tipo de movimiento es obligatorio")
        TipoMovimiento tipo,

        @NotNull(message = "la cantidad es obligatoria")
        @Min(value = 1, message = "la cantidad debe ser mayor a cero")
        Integer cantidad,

        @Size(max = 300)
        String observacion
) {
}