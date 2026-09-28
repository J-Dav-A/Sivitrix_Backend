package co.edu.uniquindio.sivitrix.producto.dto;

import co.edu.uniquindio.sivitrix.producto.CategoriaProducto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * SWR-08: datos requeridos para registrar un producto.
 * SWR-23 (reutilizado aqui): las cantidades deben ser > 0.
 */
public record ProductoRequest(

        @NotBlank(message = "el codigo es obligatorio")
        @Size(max = 20, message = "el codigo no puede superar 20 caracteres")
        String codigo,

        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 150, message = "el nombre no puede superar 150 caracteres")
        String nombre,

        @NotNull(message = "la categoria es obligatoria")
        CategoriaProducto categoria,

        @NotNull(message = "el precio de venta es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "el precio de venta debe ser mayor a 0")
        BigDecimal precioVenta,

        @NotNull(message = "el costo de adquisicion es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "el costo de adquisicion debe ser mayor a 0")
        BigDecimal costoAdquisicion,

        @NotNull(message = "el stock minimo es obligatorio")
        @Min(value = 0, message = "el stock minimo no puede ser negativo")
        Integer stockMinimo
) {
}
