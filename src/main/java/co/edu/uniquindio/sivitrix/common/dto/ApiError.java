package co.edu.uniquindio.sivitrix.common.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Forma estandar de respuesta de error para toda la API.
 * Todo endpoint que falle devuelve este mismo formato, sin importar
 * la funcionalidad (venta, inventario, proveedores, etc.).
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> detalles
) {
    public ApiError(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, List.of());
    }

    public ApiError(int status, String error, String message, String path, List<String> detalles) {
        this(LocalDateTime.now(), status, error, message, path, detalles);
    }
}
