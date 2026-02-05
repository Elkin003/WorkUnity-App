package unl.edu.cc.workunity.domain.files;

import unl.edu.cc.workunity.domain.ArchivoAdjunto;

/**
 * Clase que representa un archivo PDF adjunto a una tarea
 */
public class PDF extends ArchivoAdjunto {
    public PDF(byte[] contenido) {
        super(contenido);
    }
}

