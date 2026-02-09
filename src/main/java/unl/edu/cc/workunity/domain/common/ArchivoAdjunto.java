package unl.edu.cc.workunity.domain.common;

import jakarta.persistence.*;
import unl.edu.cc.workunity.exception.InvalidFile;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Clase abstracta para representar archivos adjuntos en tareas
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ArchivoAdjunto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float tamanio;

    @Lob
    @Column(nullable = false)
    private byte[] contenido;

    // Tamaño máximo permitido para el archivo 20 MB convertidos a bytes
    private static final int TamanioMaximoBytes = 20 * 1024 * 1024;

    public ArchivoAdjunto() {
    }

    public ArchivoAdjunto(byte[] contenido) {
        validarTamanio(contenido);
        this.contenido = contenido;
        this.tamanio = contenido.length;
    }

    public void validarTamanio(byte[] contenido) {
        if (contenido == null || contenido.length == 0) {
            throw new InvalidFile("El archivo no puede estar vacío.");
        }
        if (contenido.length > TamanioMaximoBytes) {
            throw new InvalidFile("El archivo supera los 20MB permitidos");
        }
    }

    public float getTamanio() {
        return tamanio;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        validarTamanio(contenido);
        this.contenido = contenido;
        this.tamanio = contenido.length;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ArchivoAdjunto.class.getSimpleName() + "[", "]")
                .add("tamanio=" + tamanio)
                .add("contenido=" + contenido.length + " bytes")
                .toString();
    }
}
