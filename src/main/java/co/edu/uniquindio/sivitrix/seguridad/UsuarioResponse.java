package co.edu.uniquindio.sivitrix.seguridad;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String username,
        RolUsuario rol,
        boolean principal,
        LocalDateTime creadoEn
) {
}