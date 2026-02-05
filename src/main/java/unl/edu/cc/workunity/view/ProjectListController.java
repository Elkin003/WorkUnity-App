package unl.edu.cc.workunity.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.workunity.business.WorkUnityFacade;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.Proyecto;
import unl.edu.cc.workunity.view.security.UserSession;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller para la vista de lista de proyectos
 */
@Named
@ViewScoped
public class ProjectListController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(ProjectListController.class.getName());

    @Inject
    private WorkUnityFacade workUnityFacade;

    @Inject
    private UserSession userSession;

    private List<Proyecto> projects;

    // Campos para el modal de crear proyecto
    private String newProjectName;
    private String newProjectDescription;
    private Date newProjectDeadline;

    @PostConstruct
    public void init() {
        loadProjects();
    }

    private void loadProjects() {
        try {
            if (userSession == null || userSession.getUser() == null) {
                logger.warning("No hay usuario en sesión");
                return;
            }

            Entidad currentEntity = userSession.getUser().getEntidad();
            if (currentEntity == null) {
                logger.warning("Usuario " + userSession.getUser().getName() + " no tiene Entidad asociada");
                addErrorMessage("Tu perfil no está completo");
                return;
            }

            projects = workUnityFacade.findAllProjectsByEntity(currentEntity);
            logger.info("Cargados " + projects.size() + " proyectos para: " + currentEntity.getFullName());

        } catch (Exception e) {
            logger.severe("Error cargando proyectos: " + e.getMessage());
            e.printStackTrace();
            addErrorMessage("Error al cargar proyectos: " + e.getMessage());
        }
    }

    public void createProject() {
        try {
            if (userSession.getUser() == null || userSession.getUser().getEntidad() == null) {
                addErrorMessage("No se puede crear proyecto sin perfil completo");
                logger.severe("Usuario sin entidad intentando crear proyecto");
                return;
            }

            Entidad currentEntity = userSession.getUser().getEntidad();
            logger.info("Creando proyecto para: " + currentEntity.getFullName());

            // Convertir Date a LocalDate
            LocalDate fechaLimite = newProjectDeadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Crear proyecto usando el facade
            Proyecto nuevoProyecto = workUnityFacade.createProject(currentEntity, newProjectName,
                    newProjectDescription, fechaLimite);

            logger.info(" Proyecto creado con ID: " + nuevoProyecto.getId());

            // Recargar lista de proyectos
            loadProjects();

            addSuccessMessage("Proyecto creado exitosamente");

            // Limpiar campos del modal
            clearNewProjectFields();

        } catch (IllegalArgumentException e) {
            logger.severe("Error de validación: " + e.getMessage());
            addErrorMessage("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Error creando proyecto: " + e.getMessage());
            e.printStackTrace();
            addErrorMessage("Error al crear proyecto: " + e.getMessage());
        }
    }

    public String viewProject(Proyecto proyecto) {
        return "proyecto-detalle?faces-redirect=true&projectId=" + proyecto.getId();
    }

    private void clearNewProjectFields() {
        newProjectName = null;
        newProjectDescription = null;
        newProjectDeadline = null;
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

    public List<Proyecto> getProjects() {
        return projects;
    }

    public void setProjects(List<Proyecto> projects) {
        this.projects = projects;
    }

    public String getNewProjectName() {
        return newProjectName;
    }

    public void setNewProjectName(String newProjectName) {
        this.newProjectName = newProjectName;
    }

    public String getNewProjectDescription() {
        return newProjectDescription;
    }

    public void setNewProjectDescription(String newProjectDescription) {
        this.newProjectDescription = newProjectDescription;
    }

    public Date getNewProjectDeadline() {
        return newProjectDeadline;
    }

    public void setNewProjectDeadline(Date newProjectDeadline) {
        this.newProjectDeadline = newProjectDeadline;
    }
}

