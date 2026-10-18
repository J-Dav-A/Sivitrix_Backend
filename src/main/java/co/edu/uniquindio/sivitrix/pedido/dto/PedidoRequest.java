package co.edu.uniquindio.sivitrix.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PedidoRequest(

        @NotNull(message = "el proveedor es obligatorio")
        Long proveedorId,

        @Size(max = 500)
        String observaciones,

        @NotEmpty(message = "el pedido debe tener al menos un producto")
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
