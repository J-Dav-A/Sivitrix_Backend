package co.edu.uniquindio.sivitrix.seguridad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(

        @NotBlank(message = "el usuario es obligatorio")
        @Size(min = 3, max = 30, message = "el usuario debe tener entre 3 y 30 caracteres")
        @Pattern(regexp = "[A-Za-z0-9._-]+", message = "el usuario solo puede tener letras, numeros, punto, guion y guion bajo")
        String username,

        @NotBlank(message = "la clave es obligatoria")
        @Size(min = 6, max = 60, message = "la clave debe tener entre 6 y 60 caracteres")
        String password,

        @NotNull(message = "el rol es obligatorio")
        RolUsuario rol
) {
}