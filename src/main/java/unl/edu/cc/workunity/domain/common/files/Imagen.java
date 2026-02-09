package unl.edu.cc.workunity.domain.common.files;

import jakarta.persistence.*;
import unl.edu.cc.workunity.domain.common.ArchivoAdjunto;
import unl.edu.cc.workunity.domain.common.enums.TipoImagen;

/**
 * Clase que representa una imagen adjunta a una tarea
 */
@Entity
public class Imagen extends ArchivoAdjunto {

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TipoImagen tipo;

    public Imagen() {
    }

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
