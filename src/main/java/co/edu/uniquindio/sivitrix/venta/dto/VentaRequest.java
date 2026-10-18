package co.edu.uniquindio.sivitrix.venta.dto;

import co.edu.uniquindio.sivitrix.venta.entity.TipoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VentaRequest(

        @NotNull(message = "el cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "el tipo de pago es obligatorio")
        TipoPago tipoPago,

        // Solo aplica cuando tipoPago = CREDITO.
        @Min(value = 1, message = "el numero de cuotas minimo es 1")
        @Max(value = 36, message = "el numero de cuotas maximo es 36")
        Integer numeroCuotas,

        @NotEmpty(message = "la venta debe tener al menos un producto")
        List<@Valid Linea> detalles
) {
    public record Linea(
            @NotNull(message = "el producto es obligatorio")
            Long productoId,

            @NotNull(message = "la cantidad es obligatoria")
            @Min(value = 1, message = "la cantidad debe ser mayor a cero")
            Integer cantidad
    ) {
    }
}
