package unl.edu.cc.workunity.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.workunity.business.WorkUnityFacade;
import unl.edu.cc.workunity.domain.common.Entidad;
import unl.edu.cc.workunity.domain.common.Integrante;
import unl.edu.cc.workunity.domain.common.Proyecto;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.faces.FacesUtil;
import unl.edu.cc.workunity.view.security.UserSession;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ProfileController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserSession userSession;

    @Inject
    private WorkUnityFacade workUnityFacade;

    private User currentUser;
    private int projectCount;
    private int taskCount;
    private int membershipCount;

    private String editNombre;
    private String editApellido;
    private String editTelefono;

    @PostConstruct
    public void init() {
        currentUser = userSession.getUser();
        calculateStatistics();
    }

    private void calculateStatistics() {
        if (currentUser != null && currentUser.getEntidad() != null) {

            List<Proyecto> createdProjects = currentUser.getEntidad().getProyectos();
            projectCount = createdProjects != null ? createdProjects.size() : 0;

            List<Integrante> memberships = currentUser.getEntidad().getIntegrantes();
            membershipCount = memberships != null ? memberships.size() : 0;

            taskCount = 0;
            if (memberships != null) {
                for (Integrante integrante : memberships) {
                    if (integrante.getTareas() != null) {
                        taskCount += integrante.getTareas().size();
                    }
                }
            }
        }
    }

    public void prepareEdit() {
        if (currentUser != null && currentUser.getEntidad() != null) {
            this.editNombre = currentUser.getEntidad().getNombre();
            this.editApellido = currentUser.getEntidad().getApellido();
            this.editTelefono = currentUser.getEntidad().getNumeroTelefono();
        }
    }

    public void updateProfile() {
        try {
            if (currentUser != null && currentUser.getEntidad() != null) {
                Entidad entidad = currentUser.getEntidad();
                entidad.setNombre(editNombre);
                entidad.setApellido(editApellido);
                entidad.setNumeroTelefono(editTelefono);

                workUnityFacade.saveEntity(entidad);

                FacesUtil.addSuccessMessage("Éxito", "Perfil actualizado correctamente.");
            }
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error",
                    "Error al actualizar perfil: " + e.getMessage());
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public int getProjectCount() {
        return projectCount;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public int getMembershipCount() {
        return membershipCount;
    }

    public String getEditNombre() {
        return editNombre;
    }

    public void setEditNombre(String editNombre) {
        this.editNombre = editNombre;
    }

    public String getEditApellido() {
        return editApellido;
    }

    public void setEditApellido(String editApellido) {
        this.editApellido = editApellido;
    }

    public String getEditTelefono() {
        return editTelefono;
    }

    public void setEditTelefono(String editTelefono) {
        this.editTelefono = editTelefono;
    }
}
