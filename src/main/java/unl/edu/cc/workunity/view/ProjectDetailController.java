package unl.edu.cc.workunity.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.workunity.business.WorkUnityFacade;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.Integrante;
import unl.edu.cc.workunity.domain.Proyecto;
import unl.edu.cc.workunity.domain.Tarea;
import unl.edu.cc.workunity.domain.*;
import unl.edu.cc.workunity.domain.enums.Rol;
import unl.edu.cc.workunity.exception.EntityNotFoundException;
import unl.edu.cc.workunity.view.security.UserSession;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller para la vista de detalles de un proyecto
 */
@Named
@ViewScoped
public class ProjectDetailController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(ProjectDetailController.class.getName());

    @Inject
    private WorkUnityFacade workUnityFacade;

    @Inject
    private UserSession userSession;

    // Parámetro de URL
    private Long projectId;

    // Datos del proyecto
    private Proyecto project;
    private List<Tarea> tasks;
    private List<Integrante> members;
    private Integrante currentUserIntegrante;

    // Campos para modal de nueva tarea
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
            // Cargar proyecto
            project = workUnityFacade.findProject(projectId);

            // Cargar tareas del proyecto
            tasks = workUnityFacade.findTasksByProject(project);

            // Cargar miembros del proyecto
            members = workUnityFacade.findMembersByProject(project);

            // Obtener integrante actual para validar permisos
            Entidad currentEntity = userSession.getUser().getEntidad();
            try {
                currentUserIntegrante = workUnityFacade.findIntegrantByProjectAndEntity(project, currentEntity);
            } catch (EntityNotFoundException e) {
                currentUserIntegrante = null;
                logger.warning("Usuario actual no es miembro del proyecto");
            }

            logger.info("Proyecto cargado: " + project.getNombre() + " con " + tasks.size() + " tareas y "
                    + members.size() + " miembros");

        } catch (EntityNotFoundException e) {
            logger.severe("Proyecto no encontrado: " + e.getMessage());
            addErrorMessage("Proyecto no encontrado");
        } catch (Exception e) {
            logger.severe("Error cargando datos del proyecto: " + e.getMessage());
            addErrorMessage("Error al cargar el proyecto: " + e.getMessage());
        }
    }

    public void createTask() {
        try {
            if (currentUserIntegrante == null || currentUserIntegrante.getRol() != Rol.LIDER) {
                addErrorMessage("Solo el líder puede crear tareas");
                return;
            }

            // Convertir Date a LocalDate
            LocalDate fechaLimite = newTaskDeadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Crear tarea usando el facade
            Tarea newTask = workUnityFacade.createTask(currentUserIntegrante,
                    newTaskTitle, newTaskDescription, fechaLimite);

            // Asignar si se seleccionó un integrante
            if (selectedIntegranteId != null) {
                Integrante asignado = members.stream()
                        .filter(i -> i.getId().equals(selectedIntegranteId))
                        .findFirst()
                        .orElse(null);

                if (asignado != null) {
                    workUnityFacade.assignTask(currentUserIntegrante, newTask, asignado);
                }
            }

            // Recargar datos
            loadProjectData();

            addSuccessMessage("Tarea creada exitosamente");

            // Limpiar campos del modal
            clearNewTaskFields();

        } catch (Exception e) {
            logger.severe("Error creando tarea: " + e.getMessage());
            addErrorMessage("Error al crear tarea: " + e.getMessage());
        }
    }

    public String viewTask(Tarea tarea) {
        return "tarea-detalle?faces-redirect=true&taskId=" + tarea.getId();
    }

    public String goBackToProjects() {
        return "proyectos?faces-redirect=true";
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

    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", message));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    // Getters y Setters

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

