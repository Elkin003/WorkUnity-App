package unl.edu.cc.workunity.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.workunity.business.WorkUnityFacade;
import unl.edu.cc.workunity.domain.common.Entidad;
import unl.edu.cc.workunity.domain.common.Integrante;
import unl.edu.cc.workunity.domain.common.Proyecto;
import unl.edu.cc.workunity.domain.common.Tarea;
import unl.edu.cc.workunity.domain.common.enums.Rol;
import unl.edu.cc.workunity.exception.EntityNotFoundException;
import unl.edu.cc.workunity.faces.FacesUtil;
import unl.edu.cc.workunity.view.security.UserSession;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import jakarta.faces.context.FacesContext;
import java.io.IOException;

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

    public String goBackToProjects() {
        return "projects?faces-redirect=true";
    }

    public boolean isCurrentUserLeader() {
        return currentUserIntegrante != null &&
                currentUserIntegrante.getRol() == Rol.LIDER;
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
}
