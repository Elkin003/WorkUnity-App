package unl.edu.cc.workunity.exception;

/**
 * Excepción lanzada cuando una entidad solicitada no se encuentra en la base de
 * datos
 */
public class EntityNotFoundException extends Exception {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
