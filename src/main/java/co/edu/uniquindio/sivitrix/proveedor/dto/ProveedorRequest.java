package co.edu.uniquindio.sivitrix.proveedor.dto;

import co.edu.uniquindio.sivitrix.common.validation.ValidNit;
import jakarta.validation.constraints.*;

import java.util.Set;

public record ProveedorRequest(

        @NotBlank(message = "el NIT es obligatorio")
        @ValidNit
        String nit,

        @NotBlank(message = "el nombre de la empresa es obligatorio")
        @Size(max = 150)
        String nombreEmpresa,

        @NotBlank(message = "el nombre del contacto es obligatorio")
        @Size(max = 150)
        String nombreContacto,

        @NotBlank(message = "el telefono es obligatorio")
        @Pattern(regexp = "\\d{7,15}", message = "el telefono debe contener solo digitos")
        String telefono,

        @NotBlank(message = "el correo electronico es obligatorio")
        @Email(message = "el correo electronico no tiene un formato valido")
        String email,

        // SWR-33: ids de los productos que este proveedor suministra.
        // Puede venir vacio (un proveedor se puede registrar sin productos
        // asociados todavia, y asociarlos despues con un PUT).
        Set<Long> productosIds
) {
}
