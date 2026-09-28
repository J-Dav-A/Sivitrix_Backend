package co.edu.uniquindio.sivitrix.common.exception;

import co.edu.uniquindio.sivitrix.common.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Punto unico de manejo de errores para toda la API. Cualquier controlador
 * (Producto, Cliente, y los que vengan despues) hereda este comportamiento
 * automaticamente: no hay que repetir try/catch en cada endpoint.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Recurso duplicado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Error de validacion",
                "Uno o mas campos no cumplen las reglas de validacion",
                request.getRequestURI(),
                detalles
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno",
                "Ocurrio un error inesperado. Intenta de nuevo mas tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
