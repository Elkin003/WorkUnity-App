package unl.edu.cc.workunity.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.workunity.business.service.common.CommentRepository;
import unl.edu.cc.workunity.business.service.common.EntityRepository;
import unl.edu.cc.workunity.business.service.common.IntegrantRepository;
import unl.edu.cc.workunity.business.service.common.ProjectRepository;
import unl.edu.cc.workunity.business.service.common.TaskRepository;
import unl.edu.cc.workunity.business.service.security.UserRepository;
import unl.edu.cc.workunity.domain.common.Comentario;
import unl.edu.cc.workunity.domain.common.Entidad;
import unl.edu.cc.workunity.domain.common.Integrante;
import unl.edu.cc.workunity.domain.common.Proyecto;
import unl.edu.cc.workunity.domain.common.Tarea;
import unl.edu.cc.workunity.domain.common.enums.EstadoProyecto;
import unl.edu.cc.workunity.domain.common.enums.EstadoTarea;
import unl.edu.cc.workunity.domain.common.enums.Rol;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.EntityNotFoundException;
import unl.edu.cc.workunity.exception.UnauthorizedAccessException;
import unl.edu.cc.workunity.exception.ExistingIntegrantException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class WorkUnityFacade {

    @Inject
    private EntityRepository entityRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private IntegrantRepository integrantRepository;

    @Inject
    private CommentRepository commentRepository;

    @Inject
    private UserRepository userRepository;

    public Proyecto createProject(Entidad creador, String nombre, String descripcion, LocalDate fechaLimite) {
        Proyecto proyecto = new Proyecto(nombre, descripcion, fechaLimite, creador);
        proyecto = projectRepository.save(proyecto);

        Integrante lider = new Integrante(Rol.LIDER, creador, proyecto);
        lider = integrantRepository.save(lider);

        proyecto.getMiembros().add(lider);
        creador.getIntegrantes().add(lider);
        creador.getProyectos().add(proyecto);

        return proyecto;
    }

    public Proyecto updateProject(Long projectId, String nombre, String descripcion, LocalDate fechaLimite)
            throws EntityNotFoundException {
        Proyecto proyecto = projectRepository.find(projectId);
        proyecto.setNombre(nombre);
        proyecto.setDescripcion(descripcion);
        proyecto.setFechaLimite(fechaLimite);
        return projectRepository.save(proyecto);
    }

    public void deleteProject(Long projectId) throws EntityNotFoundException {
        Proyecto proyecto = projectRepository.find(projectId);

        List<Tarea> tareas = taskRepository.findByProject(proyecto.getId());
        for (Tarea tarea : tareas) {
            deleteTask(tarea.getId());
        }

        List<Integrante> integrantes = integrantRepository.findByProject(proyecto.getId());
        for (Integrante integrante : integrantes) {
            integrantRepository.delete(integrante.getId());
        }

        projectRepository.delete(projectId);
    }

    public void changeProjectStatus(Long projectId, EstadoProyecto nuevoEstado)
            throws EntityNotFoundException {
        Proyecto proyecto = projectRepository.find(projectId);
        proyecto.setEstado(nuevoEstado);
        projectRepository.save(proyecto);
    }

    public List<Proyecto> findAllProjectsByEntity(Entidad entidad) {
        List<Integrante> memberships = entidad.getIntegrantes();
        List<Proyecto> projects = new ArrayList<>();

        if (memberships != null) {
            for (Integrante integrante : memberships) {
                projects.add(integrante.getProyecto());
            }
        }

        return projects;
    }

    public Proyecto findProject(Long id) throws EntityNotFoundException {
        return projectRepository.find(id);
    }

    public Tarea createTask(Integrante lider, String titulo, String descripcion, LocalDate fechaLimite) {
        Tarea tarea = lider.crearTarea(titulo, descripcion, fechaLimite);
        tarea = taskRepository.save(tarea);
        projectRepository.save(lider.getProyecto());
        return tarea;
    }

    public Tarea assignTask(Integrante lider, Tarea tarea, Integrante asignado) {
        lider.asignarTarea(tarea, asignado);
        taskRepository.save(tarea);
        integrantRepository.save(asignado);
        return tarea;
    }

    public void deliverTask(Tarea tarea) {
        tarea.entregar();
        taskRepository.save(tarea);
    }

    public Tarea changeTaskStatus(Long taskId, EstadoTarea nuevoEstado) throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);
        tarea.setEstado(nuevoEstado);
        return taskRepository.save(tarea);
    }

    public Tarea updateTask(Long taskId, String titulo, String descripcion, LocalDate fechaLimite)
            throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setFechaLimite(fechaLimite);
        return taskRepository.save(tarea);
    }

    public void unassignTask(Long taskId) throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);
        tarea.setIntegranteAsignado(null);
        taskRepository.save(tarea);
    }

    public void deleteTask(Long taskId) throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);

        List<Comentario> comentarios = commentRepository.findByTask(tarea.getId());
        for (Comentario comentario : comentarios) {
            commentRepository.delete(comentario.getId());
        }

        taskRepository.delete(taskId);
    }

    public List<Tarea> findTasksByProject(Proyecto proyecto) {
        return taskRepository.findByProject(proyecto.getId());
    }

    public Tarea findTask(Long id) throws EntityNotFoundException {
        return taskRepository.find(id);
    }

    public Integrante addMemberToProject(Integrante lider, Proyecto proyecto, Entidad nuevaEntidad)
            throws EntityNotFoundException {
        if (lider.getRol() != Rol.LIDER) {
            throw new UnauthorizedAccessException(
                    "Solo el líder puede agregar miembros.");
        }

        Proyecto managedProject = projectRepository.find(proyecto.getId());
        Entidad managedEntity = entityRepository.find(nuevaEntidad.getId());

        try {
            integrantRepository.findByProjectAndEntity(managedProject.getId(), managedEntity.getId());
            throw new ExistingIntegrantException(
                    "El integrante ya pertenece al proyecto.");
        } catch (EntityNotFoundException e) {
        }

        Integrante nuevoIntegrante = new Integrante(Rol.MIEMBRO, managedEntity, managedProject);
        nuevoIntegrante = integrantRepository.save(nuevoIntegrante);

        return nuevoIntegrante;
    }

    public void removeMemberFromProject(Long integranteId, Integrante lider)
            throws EntityNotFoundException, UnauthorizedAccessException {

        if (lider.getRol() != Rol.LIDER) {
            throw new UnauthorizedAccessException("Solo el líder puede eliminar miembros.");
        }

        Integrante integranteAEliminar = integrantRepository.find(integranteId);
        if (integranteAEliminar == null) {
            throw new EntityNotFoundException("Integrante no encontrado con ID " + integranteId);
        }

        if (integranteAEliminar.getRol() == Rol.LIDER) {
            throw new UnauthorizedAccessException("No se puede eliminar al líder del proyecto.");
        }

        List<Tarea> tareasAsignadas = taskRepository.findByIntegrante(integranteAEliminar.getId());
        for (Tarea tarea : tareasAsignadas) {
            tarea.setIntegranteAsignado(null);
            taskRepository.save(tarea);
        }
        commentRepository.deleteByAuthor(integranteAEliminar.getId());

        integrantRepository.deleteNative(integranteId);
    }

    public List<Integrante> findMembersByProject(Proyecto proyecto) {
        return integrantRepository.findByProject(proyecto.getId());
    }

    public Integrante findIntegrantByProjectAndEntity(Proyecto proyecto, Entidad entidad)
            throws EntityNotFoundException {
        return integrantRepository.findByProjectAndEntity(proyecto.getId(), entidad.getId());
    }

    public Comentario addCommentToTask(Tarea tarea, Integrante autor, String texto) {
        Comentario comentario = new Comentario(texto, autor);
        tarea.agregar(comentario);
        comentario = commentRepository.save(comentario);
        taskRepository.save(tarea);
        return comentario;
    }

    public List<Comentario> findCommentsByTask(Tarea tarea) {
        return commentRepository.findByTask(tarea.getId());
    }

    public void deleteComment(Long comentarioId) throws EntityNotFoundException {
        commentRepository.delete(comentarioId);
    }

    public Entidad findEntity(Long id) throws EntityNotFoundException {
        return entityRepository.find(id);
    }

    public Entidad saveEntity(Entidad entidad) {
        return entityRepository.save(entidad);
    }

    public User findUserByEmail(String email) throws EntityNotFoundException {
        return userRepository.findByEmail(email);
    }

    public User findUserByName(String name) throws EntityNotFoundException {
        return userRepository.find(name);
    }
}