package unl.edu.cc.workunity.domain.files;

import unl.edu.cc.workunity.domain.ArchivoAdjunto;

/**
 * Clase que representa un documento Word adjunto a una tarea
 */
public class Word extends ArchivoAdjunto {
    public Word(byte[] contenido) {
        super(contenido);
    }
}