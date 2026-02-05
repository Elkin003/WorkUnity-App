package unl.edu.cc.workunity.domain.files;

import unl.edu.cc.workunity.domain.ArchivoAdjunto;
import unl.edu.cc.workunity.domain.enums.TipoImagen;

/**
 * Clase que representa una imagen adjunta a una tarea
 */
public class Imagen extends ArchivoAdjunto {
    private TipoImagen tipo;

    public Imagen(byte[] contenido, TipoImagen tipo) {
        super(contenido);
        this.tipo = tipo;
    }

    public TipoImagen getTipo() {
        return tipo;
    }

    public void setTipo(TipoImagen tipo) {
        this.tipo = tipo;
    }
}
