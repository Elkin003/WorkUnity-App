package unl.edu.cc.workunity.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Elkin Jiménez
 */
public class Comentario {
    private Long id;
    private String texto;
    private LocalDate fechaCreacion;

    // Relaciones
    private Integrante autor;
    private Tarea tarea;

    public Comentario(String texto, Integrante autor) {
        this.texto = texto;
        this.autor = autor;
        this.fechaCreacion = LocalDate.now();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integrante getAutor() {
        return autor;
    }

    public void setAutor(Integrante autor) {
        this.autor = autor;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Comentario that = (Comentario) o;
        return Objects.equals(texto, that.texto) && Objects.equals(autor, that.autor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto, autor);
    }

    @Override
    public String toString() {
        return "Comentario{" +
                "id=" + id +
                ", texto='" + texto + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", autor=" + (autor != null ? autor.getEntidad().getNombre() : "null") +
                ", tarea=" + (tarea != null ? tarea.getTitulo() : "null") +
                '}';
    }
}

