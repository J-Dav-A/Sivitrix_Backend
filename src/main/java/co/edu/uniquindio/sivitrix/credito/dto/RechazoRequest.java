package co.edu.uniquindio.sivitrix.credito.dto;

import jakarta.validation.constraints.Size;

public record RechazoRequest(
        @Size(max = 300)
        String motivo
) {
}

