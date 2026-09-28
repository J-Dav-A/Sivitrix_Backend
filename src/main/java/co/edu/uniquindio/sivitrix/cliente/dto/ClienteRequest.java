package co.edu.uniquindio.sivitrix.cliente.dto;

import co.edu.uniquindio.sivitrix.common.validation.ValidNit;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

        @NotBlank(message = "el NIT es obligatorio")
        @ValidNit
        String nit,

        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 150)
        String nombre,

        @NotBlank(message = "el telefono es obligatorio")
        @Pattern(regexp = "\\d{7,15}", message = "el telefono debe contener solo digitos (7 a 15)")
        String telefono,

        @NotBlank(message = "la direccion es obligatoria")
        @Size(max = 200)
        String direccion,

        @NotBlank(message = "el correo electronico es obligatorio")
        @Email(message = "el correo electronico no tiene un formato valido")
        String email,

        // SWR-36: persona de contacto autorizada para comprar.
        @NotBlank(message = "el nombre del contacto es obligatorio")
        @Size(max = 150)
        String contactoNombre,

        @NotBlank(message = "la cedula del contacto es obligatoria")
        @Pattern(regexp = "\\d{6,12}", message = "la cedula debe contener solo digitos (6 a 12)")
        String contactoCedula
) {
}
