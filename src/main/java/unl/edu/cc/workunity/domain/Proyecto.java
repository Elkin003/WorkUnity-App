package unl.edu.cc.workunity.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Cristian Guaman
 */
public class Proyecto {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaCreacion;
    private LocalDate fechaLimite;

    // Relaciones
    private Entidad creador;
    private List<Integrante> miembros;
    private List<Tarea> tareas;

    public Proyecto(String nombre, String descripcion, LocalDate fechaLimite, Entidad creador) {
        if (fechaLimite.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha límite no puede ser anterior a la fecha de hoy.");
        }
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = LocalDate.now();
        this.fechaLimite = fechaLimite;
        this.creador = creador;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Entidad getCreador() {
        return creador;
    }

    public void setCreador(Entidad creador) {
        this.creador = creador;
    }

    public List<Integrante> getMiembros() {
        if (miembros == null) {
            miembros = new ArrayList<>();
        }
        return miembros;
    }

    public void setMiembros(List<Integrante> miembros) {
        this.miembros = miembros;
    }

    public List<Tarea> getTareas() {
        if (tareas == null) {
            tareas = new ArrayList<>();
        }
        return tareas;
    }

    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Proyecto proyecto = (Proyecto) o;
        return Objects.equals(nombre, proyecto.nombre) &&
                Objects.equals(descripcion, proyecto.descripcion) &&
                Objects.equals(creador, proyecto.creador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, descripcion, creador);
    }

    @Override
    public String toString() {
        return "\nProyecto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaLimite=" + fechaLimite +
                ", creador=" + (creador != null ? creador.getNombre() : "null") +
                ", miembros=" + getMiembros().size() +
                ", tareas=" + getTareas().size() +
                '}';
    }
}

