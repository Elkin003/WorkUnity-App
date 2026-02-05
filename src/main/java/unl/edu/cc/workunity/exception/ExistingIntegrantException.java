package unl.edu.cc.workunity.exception;

/**
 * Excepción lanzada cuando se intenta agregar un integrante que ya existe en un
 * proyecto
 */
public class ExistingIntegrantException extends RuntimeException {
    public ExistingIntegrantException(String message) {
        super(message);
    }
}