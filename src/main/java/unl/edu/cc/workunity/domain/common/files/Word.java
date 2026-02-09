package unl.edu.cc.workunity.domain.common.files;

import jakarta.persistence.Entity;
import unl.edu.cc.workunity.domain.common.ArchivoAdjunto;

/**
 * Clase que representa un documento Word adjunto a una tarea
 */
@Entity
public class Word extends ArchivoAdjunto {
    public Word() {
    }

    public Word(byte[] contenido) {
        super(contenido);
    }
}