package unl.edu.cc.workunity.domain;

import unl.edu.cc.workunity.domain.enums.EstadoTarea;
import unl.edu.cc.workunity.exception.InvalidFile;
import unl.edu.cc.workunity.exception.UnauthorizedAccessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Elkin Jiménez
 */
public class Tarea {
    private Long id;
    private String titulo;
    private String descripcion;
    private boolean entregada;
    private EstadoTarea estado;
    private LocalDate fechaAsignacion;
    private LocalDate fechaLimite;

    // Relaciones
    private Integrante integranteAsignado;
    private Proyecto proyecto;
    private List<Comentario> comentarios;
    private ArchivoAdjunto archivo;

    public Tarea(String titulo, String descripcion, LocalDate fechaLimite, Proyecto proyecto) {
        if (fechaLimite.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha límite no puede ser anterior a la fecha de hoy.");
        }
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.entregada = false;
        this.estado = EstadoTarea.EN_CURSO;
        this.fechaAsignacion = LocalDate.now();
        this.fechaLimite = fechaLimite;
        this.proyecto = proyecto;
    }

    public void agregar(Comentario comentario) {
        getComentarios();
        if (!comentarios.contains(comentario)) {
            comentarios.add(comentario);
            comentario.setTarea(this);
        }
    }

    public void agregar(ArchivoAdjunto archivo, Integrante integrante) {
        if (!this.integranteAsignado.equals(integrante)) {
            throw new UnauthorizedAccessException(
                    "Solo el integrante asignado puede adjuntar o cambiar archivos en esta tarea.");
        }
        if (archivo == null) {
            throw new InvalidFile("No hay ningún archivo adjunto.");
        }
        this.archivo = archivo;
    }

    public void entregar() {
        if (this.archivo == null) {
            throw new InvalidFile("No se puede entregar, sin un archivo adjunto");
        }
        setEntregada(true);
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEntregada() {
        return entregada;
    }

    public void setEntregada(boolean entregada) {
        this.entregada = entregada;
        if (entregada) {
            setEstado(EstadoTarea.COMPLETADA);
        } else {
            setEstado(EstadoTarea.EN_CURSO);
            if (fechaLimite.isBefore(LocalDate.now())) {
                setEstado(EstadoTarea.ATRASADA);
            }
        }
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Integrante getIntegranteAsignado() {
        return integranteAsignado;
    }

    public void setIntegranteAsignado(Integrante integranteAsignado) {
        this.integranteAsignado = integranteAsignado;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public List<Comentario> getComentarios() {
        if (comentarios == null) {
            comentarios = new ArrayList<>();
        }
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public ArchivoAdjunto getArchivo() {
        return archivo;
    }

    public void setArchivo(ArchivoAdjunto archivo) {
        this.archivo = archivo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Tarea tarea = (Tarea) o;
        return Objects.equals(titulo, tarea.titulo) &&
                Objects.equals(fechaLimite, tarea.fechaLimite) &&
                Objects.equals(proyecto, tarea.proyecto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, fechaLimite, proyecto);
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", entregada=" + entregada +
                ", estado=" + estado +
                ", fechaAsignacion=" + fechaAsignacion +
                ", fechaLimite=" + fechaLimite +
                ", integranteAsignado="
                + (integranteAsignado != null ? integranteAsignado.getEntidad().getNombre() : "Sin asignar") +
                ", proyecto=" + (proyecto != null ? proyecto.getNombre() : "null") +
                ", comentarios=" + getComentarios().size() +
                ", archivo=" + archivo +
                '}';
    }
}

