package unl.edu.cc.workunity.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.workunity.business.WorkUnityFacade;
import unl.edu.cc.workunity.domain.common.Comentario;
import unl.edu.cc.workunity.domain.common.Entidad;
import unl.edu.cc.workunity.domain.common.Integrante;
import unl.edu.cc.workunity.domain.common.Tarea;
import unl.edu.cc.workunity.domain.common.enums.EstadoTarea;
import unl.edu.cc.workunity.domain.common.enums.Rol;
import unl.edu.cc.workunity.exception.EntityNotFoundException;
import unl.edu.cc.workunity.faces.FacesUtil;
import unl.edu.cc.workunity.view.security.UserSession;

import jakarta.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class TaskDetailController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private WorkUnityFacade workUnityFacade;

    @Inject
    private UserSession userSession;

    private Long taskId;

    private Tarea task;
    private List<Comentario> comments;
    private Integrante currentUserIntegrante;

    private String newCommentText;

    @PostConstruct
    public void init() {
        if (taskId != null) {
            loadTaskData();
        }
    }

    private void loadTaskData() {
        try {
            task = workUnityFacade.findTask(taskId);
            comments = workUnityFacade.findCommentsByTask(task);

            Entidad currentEntity = userSession.getUser().getEntidad();
            List<Integrante> projectMembers = workUnityFacade
                    .findMembersByProject(task.getProyecto());

            currentUserIntegrante = projectMembers.stream()
                    .filter(i -> i.getEntidad().equals(currentEntity))
                    .findFirst()
                    .orElse(null);

            if (currentUserIntegrante == null) {
                try {
                    FacesUtil.addErrorMessageAndKeep("Error", "No tienes permiso para ver esta tarea.");
                    FacesContext.getCurrentInstance().getExternalContext().redirect("projects.xhtml");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (EntityNotFoundException e) {
            try {
                FacesUtil.addErrorMessageAndKeep("Error", "Tarea no encontrada");
                FacesContext.getCurrentInstance().getExternalContext().redirect("projects.xhtml");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al cargar la tarea: " + e.getMessage());
        }
    }

    public void addComment() {
        try {
            if (currentUserIntegrante == null) {
                FacesUtil.addErrorMessage("Error", "Debes ser miembro del proyecto para comentar");
                return;
            }

            if (newCommentText == null || newCommentText.trim().isEmpty()) {
                FacesUtil.addErrorMessage("Error", "El comentario no puede estar vacío");
                return;
            }

            workUnityFacade.addCommentToTask(task, currentUserIntegrante, newCommentText);

            loadTaskData();

            newCommentText = null;

            FacesUtil.addSuccessMessage("Éxito", "Comentario agregado");

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al agregar comentario: " + e.getMessage());
        }
    }

    public void deliverTask() {
        try {
            workUnityFacade.deliverTask(task);
            loadTaskData();
            FacesUtil.addSuccessMessage("Éxito", "Tarea entregada");
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al entregar tarea: " + e.getMessage());
        }
    }

    public void changeStatus(String newStatus) {
        try {
            EstadoTarea estadoTarea = EstadoTarea.valueOf(newStatus);
            workUnityFacade.changeTaskStatus(taskId, estadoTarea);
            loadTaskData();
            FacesUtil.addSuccessMessage("Éxito", "Estado cambiado a: " + estadoTarea);
        } catch (IllegalArgumentException e) {
            FacesUtil.addErrorMessage("Error", "Estado inválido");
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al cambiar estado: " + e.getMessage());
        }
    }

    public String deleteTask() {
        try {
            // Verificar que el usuario actual sea LÍDER del proyecto
            if (currentUserIntegrante == null || currentUserIntegrante.getRol() != Rol.LIDER) {
                FacesUtil.addErrorMessage("Error", "Solo el líder del proyecto puede eliminar tareas");
                return null;
            }

            Long projectId = task.getProyecto().getId();
            workUnityFacade.deleteTask(taskId);
            FacesUtil.addSuccessMessage("Éxito", "Tarea eliminada");
            return "project-details?faces-redirect=true&projectId=" + projectId;
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al eliminar tarea: " + e.getMessage());
            return null;
        }
    }

    public String goBackToProject() {
        if (task != null && task.getProyecto() != null) {
            return "project-details?faces-redirect=true&projectId=" + task.getProyecto().getId();
        }
        return "projects?faces-redirect=true";
    }

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
