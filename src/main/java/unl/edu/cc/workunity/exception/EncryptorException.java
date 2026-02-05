package unl.edu.cc.workunity.exception;

/**
 * Excepción lanzada cuando ocurre un error durante la encriptación
 */
public class EncryptorException extends Exception {

    public EncryptorException(String message) {
        super(message);
    }

    public EncryptorException(String message, Throwable cause) {
        super(message, cause);
    }
}