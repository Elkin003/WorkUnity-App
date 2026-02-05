package unl.edu.cc.workunity.exception;
/**
 * Excepción lanzada cuando un usuario intenta realizar una acción sin los
 * permisos necesarios
 */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
