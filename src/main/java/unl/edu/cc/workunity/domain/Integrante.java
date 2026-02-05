package unl.edu.cc.workunity.domain;

import unl.edu.cc.workunity.domain.enums.Rol;
import unl.edu.cc.workunity.exception.ExistingIntegrantException;
import unl.edu.cc.workunity.exception.UnauthorizedAccessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Integrante representa la participación de una Entidad en un Proyecto.
 *
 * @author Leonel Lima (LMess)
 */
public class Integrante {
    private Long id;
    private Rol rol;
    private Entidad entidad;
    private Proyecto proyecto;
    private List<Tarea> tareas;

    public Integrante(Rol rol, Entidad entidad, Proyecto proyecto) {
        this.rol = rol;
        this.entidad = entidad;
        this.proyecto = proyecto;
    }

    private void validarLider() {
        if (rol != Rol.LIDER) {
            throw new UnauthorizedAccessException("No tiene permiso para modificar");
        }
    }

    public void editarProyecto(String nuevoNombre, String nuevaDescripcion, LocalDate nuevaFechaLimite) {
        validarLider();
        proyecto.setNombre(nuevoNombre);
        proyecto.setDescripcion(nuevaDescripcion);
        proyecto.setFechaLimite(nuevaFechaLimite);
    }

    public void agregarIntegrante(Entidad entidad) {
        validarLider();
        Integrante integranteNuevo = new Integrante(Rol.MIEMBRO, entidad, proyecto);
        if (!proyecto.getMiembros().contains(integranteNuevo)) {
            proyecto.getMiembros().add(integranteNuevo);
            entidad.getIntegrantes().add(integranteNuevo);
        } else {
            throw new ExistingIntegrantException("El integrante ya pertenece al proyecto.");
        }
    }

    public Tarea crearTarea(String titulo, String descripcion, LocalDate fechaLimite) {
        validarLider();
        Tarea tarea = new Tarea(titulo, descripcion, fechaLimite, proyecto);
        proyecto.getTareas().add(tarea);
        return tarea;
    }

    public void asignarTarea(Tarea tarea, Integrante integranteAsignado) {
        validarLider();
        if (!proyecto.getMiembros().contains(integranteAsignado)) {
            throw new UnauthorizedAccessException("El integrante no pertenece al Proyecto");
        }
        tarea.setIntegranteAsignado(integranteAsignado);
        integranteAsignado.getTareas();
        integranteAsignado.tareas.add(tarea);
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Entidad getEntidad() {
        return entidad;
    }

    public void setEntidad(Entidad entidad) {
        this.entidad = entidad;
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
        Integrante that = (Integrante) o;
        return rol == that.rol &&
                Objects.equals(entidad, that.entidad) &&
                Objects.equals(proyecto, that.proyecto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rol, entidad, proyecto);
    }

    @Override
    public String toString() {
        return "Integrante{" +
                "id=" + id +
                ", rol=" + rol +
                ", entidad=" + (entidad != null ? entidad.getNombre() : "null") +
                ", proyecto=" + (proyecto != null ? proyecto.getNombre() : "null") +
                ", tareas=" + getTareas().size() +
                '}';
    }
}

