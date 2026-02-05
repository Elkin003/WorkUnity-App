package unl.edu.cc.workunity.exception;

/**
 * Excepción lanzada cuando hay un problema con un archivo adjunto
 */
public class InvalidFile extends RuntimeException {
    public InvalidFile(String message) {
        super(message);
    }
}

