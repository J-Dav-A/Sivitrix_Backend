package co.edu.uniquindio.sivitrix.cliente.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String nit,
        String nombre,
        String telefono,
        String direccion,
        String email,
        String contactoNombre,
        String contactoCedula,
        BigDecimal saldoCredito,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {
}
