package unl.edu.cc.workunity.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.workunity.business.service.security.*;
import unl.edu.cc.workunity.domain.*;
import unl.edu.cc.workunity.domain.enums.EstadoTarea;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * Facade principal de WorkUnity que orquesta operaciones de alto nivel
 * Coordina los repositorios y la lógica de negocio del dominio
 */
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

    // ======= PROYECTOS =======

    /**
     * Crea un nuevo proyecto usando la lógica de negocio de Entidad
     * Automáticamente crea el integrante líder
     */
    public Proyecto createProject(Entidad creador, String nombre, String descripcion, LocalDate fechaLimite) {
        // Usa el método de negocio del dominio que crea proyecto e integrante líder
        creador.crearProyecto(nombre, descripcion, fechaLimite);

        // Obtener el proyecto recién creado (el último en la lista)
        List<Proyecto> proyectos = creador.getProyectos();
        Proyecto proyecto = proyectos.get(proyectos.size() - 1);

        // Guardar proyecto en repositorio
        proyecto = projectRepository.save(proyecto);

        // Guardar el integrante líder
        List<Integrante> miembros = proyecto.getMiembros();
        if (!miembros.isEmpty()) {
            Integrante lider = miembros.get(miembros.size() - 1);
            integrantRepository.save(lider);
        }

        // Actualizar entidad
        entityRepository.save(creador);

        return proyecto;
    }

    /**
     * Actualiza la información de un proyecto
     */
    public Proyecto updateProject(Long projectId, String nombre, String descripcion, LocalDate fechaLimite)
            throws EntityNotFoundException {
        Proyecto proyecto = projectRepository.find(projectId);
        proyecto.setNombre(nombre);
        proyecto.setDescripcion(descripcion);
        proyecto.setFechaLimite(fechaLimite);
        return projectRepository.save(proyecto);
    }

    /**
     * Elimina un proyecto y todas sus dependencias (tareas, integrantes,
     * comentarios)
     */
    public void deleteProject(Long projectId) throws EntityNotFoundException {
        Proyecto proyecto = projectRepository.find(projectId);

        // Eliminar todos los comentarios de las tareas del proyecto
        List<Tarea> tareas = taskRepository.findByProject(proyecto);
        for (Tarea tarea : tareas) {
            List<Comentario> comentarios = commentRepository.findByTask(tarea);
            for (Comentario comentario : comentarios) {
                commentRepository.delete(comentario.getId());
            }
            taskRepository.delete(tarea.getId());
        }

        // Eliminar todos los integrantes del proyecto
        List<Integrante> integrantes = integrantRepository.findByProject(proyecto);
        for (Integrante integrante : integrantes) {
            integrantRepository.delete(integrante.getId());
        }

        // Eliminar el proyecto
        projectRepository.delete(projectId);
    }

    /**
     * Encuentra todos los proyectos de una entidad
     */
    public List<Proyecto> findAllProjectsByEntity(Entidad entidad) {
        return entidad.listarProyectos();
    }

    /**
     * Encuentra un proyecto por ID
     */
    public Proyecto findProject(Long id) throws EntityNotFoundException {
        return projectRepository.find(id);
    }

    // ======= TAREAS =======

    /**
     * Crea una nueva tarea usando la lógica de negocio de Integrante
     * Solo el líder puede crear tareas
     */
    public Tarea createTask(Integrante lider, String titulo, String descripcion, LocalDate fechaLimite) {
        // El método crearTarea valida que sea líder
        Tarea tarea = lider.crearTarea(titulo, descripcion, fechaLimite);

        // Guardar tarea en repositorio
        tarea = taskRepository.save(tarea);

        // Actualizar proyecto
        projectRepository.save(lider.getProyecto());

        return tarea;
    }

    /**
     * Asigna una tarea a un integrante
     * Solo el líder puede asignar tareas
     */
    public Tarea assignTask(Integrante lider, Tarea tarea, Integrante asignado) {
        // El método asignarTarea valida que sea líder y que el asignado pertenezca al
        // proyecto
        lider.asignarTarea(tarea, asignado);

        // Actualizar tarea y integrante en repositorios
        taskRepository.save(tarea);
        integrantRepository.save(asignado);

        return tarea;
    }

    /**
     * Marca una tarea como entregada
     * Valida que exista archivo adjunto
     */
    public void deliverTask(Tarea tarea) {
        // El método entregar valida que exista archivo adjunto
        tarea.entregar();
        taskRepository.save(tarea);
    }

    /**
     * Cambia el estado de una tarea
     */
    public Tarea changeTaskStatus(Long taskId, EstadoTarea nuevoEstado) throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);
        tarea.setEstado(nuevoEstado);
        return taskRepository.save(tarea);
    }

    /**
     * Actualiza una tarea
     */
    public Tarea updateTask(Long taskId, String titulo, String descripcion, LocalDate fechaLimite)
            throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setFechaLimite(fechaLimite);
        return taskRepository.save(tarea);
    }

    /**
     * Elimina una tarea y sus comentarios
     */
    public void deleteTask(Long taskId) throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);

        // Eliminar todos los comentarios de la tarea
        List<Comentario> comentarios = commentRepository.findByTask(tarea);
        for (Comentario comentario : comentarios) {
            commentRepository.delete(comentario.getId());
        }

        taskRepository.delete(taskId);
    }

    /**
     * Encuentra todas las tareas de un proyecto
     */
    public List<Tarea> findTasksByProject(Proyecto proyecto) {
        return taskRepository.findByProject(proyecto);
    }

    /**
     * Encuentra una tarea por ID
     */
    public Tarea findTask(Long id) throws EntityNotFoundException {
        return taskRepository.find(id);
    }

    // ======= INTEGRANTES =======

    /**
     * Agrega un nuevo miembro a un proyecto
     * Solo el líder puede agregar miembros
     */
    public Integrante addMemberToProject(Integrante lider, Proyecto proyecto, Entidad nuevaEntidad) {
        // El método agregarIntegrante valida que sea líder y que no exista el
        // integrante
        lider.agregarIntegrante(nuevaEntidad);

        // Obtener el integrante recién creado (el último en la lista)
        List<Integrante> miembros = proyecto.getMiembros();
        Integrante nuevoIntegrante = miembros.get(miembros.size() - 1);

        // Guardar el nuevo integrante
        nuevoIntegrante = integrantRepository.save(nuevoIntegrante);

        // Actualizar entidad y proyecto
        entityRepository.save(nuevaEntidad);
        projectRepository.save(proyecto);

        return nuevoIntegrante;
    }

    /**
     * Elimina un miembro de un proyecto
     * Las tareas asignadas quedan sin asignar
     */
    public void removeMemberFromProject(Long integranteId) throws EntityNotFoundException {
        Integrante integrante = integrantRepository.find(integranteId);

        // Desasignar todas las tareas del integrante
        List<Tarea> tareas = taskRepository.findByIntegrante(integrante);
        for (Tarea tarea : tareas) {
            tarea.setIntegranteAsignado(null);
            taskRepository.save(tarea);
        }

        integrantRepository.delete(integranteId);
    }

    /**
     * Encuentra todos los miembros de un proyecto
     */
    public List<Integrante> findMembersByProject(Proyecto proyecto) {
        return integrantRepository.findByProject(proyecto);
    }

    /**
     * Encuentra un integrante por proyecto y entidad
     */
    public Integrante findIntegrantByProjectAndEntity(Proyecto proyecto, Entidad entidad)
            throws EntityNotFoundException {
        return integrantRepository.findByProjectAndEntity(proyecto, entidad);
    }

    // ======= COMENTARIOS =======

    /**
     * Agrega un comentario a una tarea
     */
    public Comentario addCommentToTask(Tarea tarea, Integrante autor, String texto) {
        Comentario comentario = new Comentario(texto, autor);

        // Usa el método de negocio del dominio
        tarea.agregar(comentario);

        // Guardar comentario
        comentario = commentRepository.save(comentario);

        // Actualizar tarea
        taskRepository.save(tarea);
        return comentario;
    }

    /**
     * Encuentra todos los comentarios de una tarea
     */
    public List<Comentario> findCommentsByTask(Tarea tarea) {
        return commentRepository.findByTask(tarea);
    }

    /**
     * Elimina un comentario
     */
    public void deleteComment(Long comentarioId) throws EntityNotFoundException {
        commentRepository.delete(comentarioId);
    }

    // ======= ENTIDADES =======

    /**
     * Encuentra una entidad por ID
     */
    public Entidad findEntity(Long id) throws EntityNotFoundException {
        return entityRepository.find(id);
    }

    /**
     * Guarda o actualiza una entidad
     */
    public Entidad saveEntity(Entidad entidad) {
        return entityRepository.save(entidad);
    }
}