package unl.edu.cc.workunity.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.workunity.business.WorkUnityFacade;
import unl.edu.cc.workunity.domain.common.Entidad;
import unl.edu.cc.workunity.domain.common.Proyecto;
import unl.edu.cc.workunity.faces.FacesUtil;
import unl.edu.cc.workunity.view.security.UserSession;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class ProjectListController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private WorkUnityFacade workUnityFacade;

    @Inject
    private UserSession userSession;

    private List<Proyecto> projects;

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
                return;
            }

            Entidad currentEntity = userSession.getUser().getEntidad();
            if (currentEntity == null) {
                FacesUtil.addErrorMessage("Error", "Tu perfil no está completo");
                return;
            }

            projects = workUnityFacade.findAllProjectsByEntity(currentEntity);

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al cargar proyectos: " + e.getMessage());
        }
    }

    public void createProject() {
        try {
            if (userSession.getUser() == null || userSession.getUser().getEntidad() == null) {
                FacesUtil.addErrorMessage("Error", "No se puede crear proyecto sin perfil completo");
                return;
            }

            Entidad currentEntity = userSession.getUser().getEntidad();

            LocalDate fechaLimite = newProjectDeadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            workUnityFacade.createProject(currentEntity, newProjectName,
                    newProjectDescription, fechaLimite);

            loadProjects();

            FacesUtil.addSuccessMessage("Éxito", "Proyecto creado exitosamente");

            clearNewProjectFields();

        } catch (IllegalArgumentException e) {
            FacesUtil.addErrorMessage("Error", "Error de validación: " + e.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al crear proyecto: " + e.getMessage());
        }
    }

    public String viewProject(Proyecto proyecto) {
        return "project-details?faces-redirect=true&projectId=" + proyecto.getId();
    }

    private void clearNewProjectFields() {
        newProjectName = null;
        newProjectDescription = null;
        newProjectDeadline = null;
    }

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
