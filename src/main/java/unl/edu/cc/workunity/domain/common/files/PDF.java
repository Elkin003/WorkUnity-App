package unl.edu.cc.workunity.domain.common.files;

import jakarta.persistence.Entity;
import unl.edu.cc.workunity.domain.common.ArchivoAdjunto;

/**
 * Clase que representa un archivo PDF adjunto a una tarea
 */
@Entity
public class PDF extends ArchivoAdjunto {
    public PDF() {
    }

    public PDF(byte[] contenido) {
        super(contenido);
    }
}
