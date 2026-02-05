package unl.edu.cc.workunity.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.workunity.business.WorkUnityFacade;
import unl.edu.cc.workunity.domain.Comentario;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.Integrante;
import unl.edu.cc.workunity.domain.Tarea;
import unl.edu.cc.workunity.domain.*;
import unl.edu.cc.workunity.domain.enums.EstadoTarea;
import unl.edu.cc.workunity.exception.EntityNotFoundException;
import unl.edu.cc.workunity.view.security.UserSession;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller para la vista de detalles de una tarea
 */
@Named
@ViewScoped
public class TaskDetailController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(TaskDetailController.class.getName());

    @Inject
    private WorkUnityFacade workUnityFacade;

    @Inject
    private UserSession userSession;

    // Parámetro de URL
    private Long taskId;

    // Datos de la tarea
    private Tarea task;
    private List<Comentario> comments;
    private Integrante currentUserIntegrante;

    // Campo para agregar comentario
    private String newCommentText;

    @PostConstruct
    public void init() {
        if (taskId != null) {
            loadTaskData();
        }
    }

    private void loadTaskData() {
        try {
            // Cargar tarea
            task = workUnityFacade.findTask(taskId);

            // Cargar comentarios
            comments = workUnityFacade.findCommentsByTask(task);

            // Obtener integrante actual en el proyecto
            Entidad currentEntity = userSession.getUser().getEntidad();
            List<Integrante> projectMembers = workUnityFacade
                    .findMembersByProject(task.getProyecto());

            currentUserIntegrante = projectMembers.stream()
                    .filter(i -> i.getEntidad().equals(currentEntity))
                    .findFirst()
                    .orElse(null);

            if (currentUserIntegrante == null) {
                logger.warning("Usuario actual no es miembro del proyecto de esta tarea");
            }

            logger.info("Tarea cargada: " + task.getTitulo() + " con " + comments.size() + " comentarios");

        } catch (EntityNotFoundException e) {
            logger.severe("Tarea no encontrada: " + e.getMessage());
            addErrorMessage("Tarea no encontrada");
        } catch (Exception e) {
            logger.severe("Error cargando datos de la tarea: " + e.getMessage());
            addErrorMessage("Error al cargar la tarea: " + e.getMessage());
        }
    }

    public void addComment() {
        try {
            if (currentUserIntegrante == null) {
                addErrorMessage("Debes ser miembro del proyecto para comentar");
                return;
            }

            if (newCommentText == null || newCommentText.trim().isEmpty()) {
                addErrorMessage("El comentario no puede estar vacío");
                return;
            }

            // Agregar comentario usando el facade
            workUnityFacade.addCommentToTask(task, currentUserIntegrante, newCommentText);

            // Recargar datos
            loadTaskData();

            // Limpiar campo
            newCommentText = null;

            addSuccessMessage("Comentario agregado");

        } catch (Exception e) {
            logger.severe("Error agregando comentario: " + e.getMessage());
            addErrorMessage("Error al agregar comentario: " + e.getMessage());
        }
    }

    public void deliverTask() {
        try {
            workUnityFacade.deliverTask(task);
            loadTaskData();
            addSuccessMessage("Tarea entregada");
        } catch (Exception e) {
            logger.severe("Error entregando tarea: " + e.getMessage());
            addErrorMessage("Error al entregar tarea: " + e.getMessage());
        }
    }

    public void changeStatus(String newStatus) {
        try {
            EstadoTarea estadoTarea = EstadoTarea.valueOf(newStatus);
            workUnityFacade.changeTaskStatus(taskId, estadoTarea);
            loadTaskData();
            addSuccessMessage("Estado cambiado a: " + estadoTarea);
        } catch (IllegalArgumentException e) {
            addErrorMessage("Estado inválido");
        } catch (Exception e) {
            logger.severe("Error cambiando estado: " + e.getMessage());
            addErrorMessage("Error al cambiar estado: " + e.getMessage());
        }
    }

    public String deleteTask() {
        try {
            Long projectId = task.getProyecto().getId();
            workUnityFacade.deleteTask(taskId);
            addSuccessMessage("Tarea eliminada");
            return "proyecto-detalle?faces-redirect=true&projectId=" + projectId;
        } catch (Exception e) {
            logger.severe("Error eliminando tarea: " + e.getMessage());
            addErrorMessage("Error al eliminar tarea: " + e.getMessage());
            return null;
        }
    }

    public String goBackToProject() {
        if (task != null && task.getProyecto() != null) {
            return "proyecto-detalle?faces-redirect=true&projectId=" + task.getProyecto().getId();
        }
        return "proyectos?faces-redirect=true";
    }

    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", message));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    // Getters y Setters

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Tarea getTask() {
        return task;
    }

    public void setTask(Tarea task) {
        this.task = task;
    }

    public List<Comentario> getComments() {
        return comments;
    }

    public void setComments(List<Comentario> comments) {
        this.comments = comments;
    }

    public String getNewCommentText() {
        return newCommentText;
    }

    public void setNewCommentText(String newCommentText) {
        this.newCommentText = newCommentText;
    }

    public Integrante getCurrentUserIntegrante() {
        return currentUserIntegrante;
    }
}

