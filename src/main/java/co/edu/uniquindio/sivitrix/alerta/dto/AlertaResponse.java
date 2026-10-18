package co.edu.uniquindio.sivitrix.alerta.dto;

import co.edu.uniquindio.sivitrix.alerta.entity.NivelAlerta;

import java.time.LocalDateTime;

public record AlertaResponse(
        Long id,
        Long productoId,
        String productoCodigo,
        String productoNombre,
        NivelAlerta nivel,
        Integer stockActual,
        Integer stockAlGenerar,
        Integer stockMinimo,
        Integer faltante,
        LocalDateTime fechaGeneracion,
        Boolean activa
) {
}