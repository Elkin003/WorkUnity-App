package unl.edu.cc.workunity.exception;


/**
 * Excepción lanzada cuando ya existe una entidad con los mismos datos
 */
public class AlreadyEntityException extends Exception {

    public AlreadyEntityException(String message) {
        super(message);
    }

    public AlreadyEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}

