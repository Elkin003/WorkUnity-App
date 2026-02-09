package unl.edu.cc.workunity.domain.common;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Cristian Guaman
 */
@Entity
@Table(name = "proyecto")
public class Proyecto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @NotNull
    @Column(nullable = false)
    private LocalDate fechaCreacion;

    @NotNull
    @Column(nullable = false)
    private LocalDate fechaLimite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id", nullable = false)
    @NotNull
    private Entidad creador;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Integrante> miembros;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarea> tareas;

    public Proyecto() {
    }

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
                '}';
    }
}
