package unl.edu.cc.workunity.exception;

/**
 * Excepción lanzada cuando las credenciales de autenticación son inválidas
 */
public class CredentialInvalidException extends Exception {

    public CredentialInvalidException() {
        super("Credenciales inválidas");
    }

    public CredentialInvalidException(String message) {
        super(message);
    }

    public CredentialInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}