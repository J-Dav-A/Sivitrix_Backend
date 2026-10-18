package co.edu.uniquindio.sivitrix.common.exception;

/**
 * Se lanza cuando una operacion viola una regla de negocio (stock
 * insuficiente, cupo de credito excedido, pedido ya recibido, etc.).
 * El GlobalExceptionHandler la convierte en HTTP 422.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
