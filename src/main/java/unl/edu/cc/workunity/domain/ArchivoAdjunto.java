package unl.edu.cc.workunity.domain;

import unl.edu.cc.workunity.exception.InvalidFile;

import java.util.StringJoiner;

/**
 * Clase abstracta para representar archivos adjuntos en tareas
 */
public abstract class ArchivoAdjunto {
    protected float tamanio;
    protected byte[] contenido;

    // Tamaño máximo permitido para el archivo 20 MB convertidos a bytes
    private static final int TamanioMaximoBytes = 20 * 1024 * 1024;

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

