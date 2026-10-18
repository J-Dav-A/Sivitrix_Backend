package co.edu.uniquindio.sivitrix.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Mercancia que llego del proveedor (total o parcial). */
public record RecepcionRequest(

        @NotEmpty(message = "indica al menos una linea recibida")
        List<@Valid Linea> lineas
) {
    public record Linea(
            @NotNull(message = "el detalle es obligatorio")
            Long detalleId,

            @NotNull(message = "la cantidad es obligatoria")
            @Min(value = 1, message = "la cantidad debe ser mayor a cero")
            Integer cantidad
    ) {
    }
}
