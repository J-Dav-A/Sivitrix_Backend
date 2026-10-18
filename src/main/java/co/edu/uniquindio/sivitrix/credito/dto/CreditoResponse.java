package co.edu.uniquindio.sivitrix.credito.dto;

import co.edu.uniquindio.sivitrix.credito.entity.EstadoCredito;
import co.edu.uniquindio.sivitrix.credito.entity.EstadoCuota;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreditoResponse(
        Long id,
        Long ventaId,
        String ventaNumero,
        Long clienteId,
        String clienteNit,
        String clienteNombre,
        BigDecimal valorTotal,
        Integer numeroCuotas,
        EstadoCredito estado,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaResolucion,
        String observacion,
        BigDecimal saldoPendiente,
        List<CuotaResponse> cuotas
) {
    public record CuotaResponse(
            Long id,
            Integer numero,
            BigDecimal valor,
            LocalDate fechaLimite,
            EstadoCuota estado,
            LocalDateTime fechaPago
    ) {
    }
}
