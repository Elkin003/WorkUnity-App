package unl.edu.cc.workunity.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;
import unl.edu.cc.workunity.business.WorkUnityFacade;
import unl.edu.cc.workunity.domain.common.Entidad;
import unl.edu.cc.workunity.domain.common.Integrante;
import unl.edu.cc.workunity.domain.common.Proyecto;
import unl.edu.cc.workunity.domain.common.Tarea;
import unl.edu.cc.workunity.domain.common.enums.Rol;
import unl.edu.cc.workunity.domain.common.enums.EstadoProyecto;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.EntityNotFoundException;
import unl.edu.cc.workunity.exception.ExistingIntegrantException;
import unl.edu.cc.workunity.exception.UnauthorizedAccessException;
import unl.edu.cc.workunity.faces.FacesUtil;
import unl.edu.cc.workunity.view.security.UserSession;
import java.io.Serializable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class ProjectDetailController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private WorkUnityFacade workUnityFacade;

    @Inject
    private UserSession userSession;

    private Long projectId;

    private Proyecto project;
    private List<Tarea> tasks;
    private List<Integrante> members;
    private Integrante currentUserIntegrante;

    private String newTaskTitle;
    private String newTaskDescription;
    private Date newTaskDeadline;
    private Long selectedIntegranteId;
    private String inviteEmailOrUsername;

    private String editProjectName;
    private String editProjectDescription;
    private Date editProjectDeadline;

    @PostConstruct
    public void init() {
        if (projectId != null) {
            loadProjectData();
        }
    }

    private void loadProjectData() {
        try {
            project = workUnityFacade.findProject(projectId);
            Entidad currentEntity = userSession.getUser().getEntidad();
            currentUserIntegrante = workUnityFacade.findIntegrantByProjectAndEntity(project, currentEntity);

            if (currentUserIntegrante == null) {
                FacesUtil.addErrorMessageAndKeep("Error", "No tienes permiso para ver este proyecto.");
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("projects.xhtml");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }

            tasks = workUnityFacade.findTasksByProject(project);
            members = workUnityFacade.findMembersByProject(project);

        } catch (EntityNotFoundException e) {
            if (project == null) {
                try {
                    FacesUtil.addErrorMessageAndKeep("Error", "Proyecto no encontrado");
                    FacesContext.getCurrentInstance().getExternalContext().redirect("projects.xhtml");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    FacesUtil.addErrorMessageAndKeep("Error", "No tienes permiso para ver este proyecto.");
                    FacesContext.getCurrentInstance().getExternalContext().redirect("projects.xhtml");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al cargar el proyecto: " + e.getMessage());
        }
    }

    public void createTask() {
        try {
            if (currentUserIntegrante == null || currentUserIntegrante.getRol() != Rol.LIDER) {
                FacesUtil.addErrorMessage("Error", "Solo el líder puede crear tareas");
                return;
            }

            LocalDate fechaLimite = newTaskDeadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            Tarea newTask = workUnityFacade.createTask(currentUserIntegrante,
                    newTaskTitle, newTaskDescription, fechaLimite);

            if (selectedIntegranteId != null) {
                Integrante asignado = members.stream()
                        .filter(i -> i.getId().equals(selectedIntegranteId))
                        .findFirst()
                        .orElse(null);

                if (asignado != null) {
                    workUnityFacade.assignTask(currentUserIntegrante, newTask, asignado);
                }
            }

            loadProjectData();

            FacesUtil.addSuccessMessage("Éxito", "Tarea creada exitosamente");

            clearNewTaskFields();

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al crear tarea: " + e.getMessage());
        }
    }

    public String viewTask(Tarea tarea) {
        return "task-details?faces-redirect=true&taskId=" + tarea.getId();
    }

    public boolean isCurrentUserLeader() {
        return currentUserIntegrante != null &&
                currentUserIntegrante.getRol() == Rol.LIDER;
    }

    public void inviteMember() {
        try {
            if (!isCurrentUserLeader()) {
                FacesUtil.addErrorMessage("Error", "Solo el líder puede invitar miembros.");
                return;
            }

            if (inviteEmailOrUsername == null || inviteEmailOrUsername.trim().isEmpty()) {
                FacesUtil.addErrorMessage("Error", "Ingresa un email o nombre de usuario.");
                return;
            }

            User userFound = null;
            try {
                userFound = workUnityFacade.findUserByEmail(inviteEmailOrUsername);
            } catch (EntityNotFoundException e) {
                try {
                    userFound = workUnityFacade.findUserByName(inviteEmailOrUsername);
                } catch (EntityNotFoundException ex) {
                    FacesUtil.addErrorMessage("Error", "Usuario no encontrado.");
                    return;
                }
            }

            Entidad newMemberEntity = userFound.getEntidad();

            workUnityFacade.addMemberToProject(currentUserIntegrante, project, newMemberEntity);

            members = workUnityFacade.findMembersByProject(project);
            inviteEmailOrUsername = null;

            FacesUtil.addSuccessMessage("Éxito", "Usuario agregado al proyecto.");

        } catch (ExistingIntegrantException e) {
            FacesUtil.addErrorMessage("Info", "El usuario ya es miembro del proyecto.");
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("ConstraintViolationException")) {
                FacesUtil.addErrorMessage("Info", "El usuario ya es miembro del proyecto.");
            } else {
                FacesUtil.addErrorMessage("Error", "Error al invitar usuario: " + msg);
                e.printStackTrace();
            }
        }
    }

    public void removeMember(Integrante miembro) {
        try {
            if (!isCurrentUserLeader()) {
                FacesUtil.addErrorMessage("Error", "Solo el líder puede eliminar miembros.");
                return;
            }

            workUnityFacade.removeMemberFromProject(miembro.getId(), currentUserIntegrante);

            members = workUnityFacade.findMembersByProject(project);

            FacesUtil.addSuccessMessage("Éxito",
                    "El integrante " + miembro.getEntidad().getFullName() + " ha sido eliminado del proyecto.");

        } catch (UnauthorizedAccessException e) {
            FacesUtil.addErrorMessage("Error", e.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al eliminar integrante: " + e.getMessage());
        }
    }

    public void prepareEditProject() {
        if (project != null) {
            this.editProjectName = project.getNombre();
            this.editProjectDescription = project.getDescripcion();
            this.editProjectDeadline = Date.from(project.getFechaLimite()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }

    public void updateProject() {
        try {
            if (!isCurrentUserLeader()) {
                FacesUtil.addErrorMessage("Error", "Solo el líder puede editar el proyecto.");
                return;
            }

            LocalDate fechaLimite = editProjectDeadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            project = workUnityFacade.updateProject(project.getId(), editProjectName,
                    editProjectDescription, fechaLimite);

            FacesUtil.addSuccessMessage("Éxito", "Proyecto actualizado correctamente.");
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al actualizar proyecto: " + e.getMessage());
        }
    }

    public String deleteProject() {
        try {
            if (!isCurrentUserLeader()) {
                FacesUtil.addErrorMessage("Error", "Solo el líder puede eliminar el proyecto.");
                return null;
            }

            workUnityFacade.deleteProject(project.getId());
            FacesUtil.addSuccessMessageAndKeep("Éxito", "Proyecto eliminado correctamente.");
            return "projects?faces-redirect=true";
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al eliminar proyecto: " + e.getMessage());
            return null;
        }
    }

    public void toggleProjectStatus() {
        try {
            if (!isCurrentUserLeader()) {
                FacesUtil.addErrorMessage("Error", "Solo el líder puede cambiar el estado del proyecto.");
                return;
            }

            EstadoProyecto nuevoEstado;
            if (project.getEstado() == EstadoProyecto.COMPLETADO) {
                nuevoEstado = EstadoProyecto.ACTIVO;
            } else {
                nuevoEstado = EstadoProyecto.COMPLETADO;
            }

            workUnityFacade.changeProjectStatus(project.getId(), nuevoEstado);
            project.setEstado(nuevoEstado);

            String msg;
            if (nuevoEstado == EstadoProyecto.COMPLETADO) {
                msg = "Proyecto marcado como COMPLETADO.";
            } else {
                msg = "Proyecto reactivado.";
            }

            FacesUtil.addSuccessMessage("Estado Actualizado", msg);

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al cambiar estado: " + e.getMessage());
        }
    }

    private void clearNewTaskFields() {
        newTaskTitle = null;
        newTaskDescription = null;
        newTaskDeadline = null;
        selectedIntegranteId = null;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Proyecto getProject() {
        return project;
    }

    public void setProject(Proyecto project) {
        this.project = project;
    }

    public List<Tarea> getTasks() {
        return tasks;
    }

    public void setTasks(List<Tarea> tasks) {
        this.tasks = tasks;
    }

    public List<Integrante> getMembers() {
        return members;
    }

    public void setMembers(List<Integrante> members) {
        this.members = members;
    }

    public String getNewTaskTitle() {
        return newTaskTitle;
    }

    public void setNewTaskTitle(String newTaskTitle) {
        this.newTaskTitle = newTaskTitle;
    }

    public String getNewTaskDescription() {
        return newTaskDescription;
    }

    public void setNewTaskDescription(String newTaskDescription) {
        this.newTaskDescription = newTaskDescription;
    }

    public Date getNewTaskDeadline() {
        return newTaskDeadline;
    }

    public void setNewTaskDeadline(Date newTaskDeadline) {
        this.newTaskDeadline = newTaskDeadline;
    }

    public Long getSelectedIntegranteId() {
        return selectedIntegranteId;
    }

    public void setSelectedIntegranteId(Long selectedIntegranteId) {
        this.selectedIntegranteId = selectedIntegranteId;
    }

    public Integrante getCurrentUserIntegrante() {
        return currentUserIntegrante;
    }

    public String getInviteEmailOrUsername() {
        return inviteEmailOrUsername;
    }

    public void setInviteEmailOrUsername(String inviteEmailOrUsername) {
        this.inviteEmailOrUsername = inviteEmailOrUsername;
    }

    public String getEditProjectName() {
        return editProjectName;
    }

    public void setEditProjectName(String editProjectName) {
        this.editProjectName = editProjectName;
    }

    public String getEditProjectDescription() {
        return editProjectDescription;
    }

    public void setEditProjectDescription(String editProjectDescription) {
        this.editProjectDescription = editProjectDescription;
    }

    public Date getEditProjectDeadline() {
        return editProjectDeadline;
    }

    public void setEditProjectDeadline(Date editProjectDeadline) {
        this.editProjectDeadline = editProjectDeadline;
    }

    public String backToProjects() {
        return "projects?faces-redirect=true";
    }
}
