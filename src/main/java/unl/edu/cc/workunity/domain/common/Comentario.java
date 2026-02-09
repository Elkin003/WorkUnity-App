package unl.edu.cc.workunity.domain.common;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Elkin Jiménez
 */
@Entity
@Table(name = "comentario")
public class Comentario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Column(nullable = false, length = 1000)
    private String texto;

    @NotNull
    @Column(nullable = false)
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    @NotNull
    private Integrante autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id", nullable = false)
    @NotNull
    private Tarea tarea;

    public Comentario() {
    }

    public Comentario(String texto, Integrante autor) {
        this.texto = texto;
        this.autor = autor;
        this.fechaCreacion = LocalDate.now();
    }

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
