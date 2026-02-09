package unl.edu.cc.workunity.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.workunity.domain.common.Integrante;
import unl.edu.cc.workunity.domain.common.Proyecto;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.view.security.UserSession;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ProfileController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserSession userSession;

    private User currentUser;
    private int projectCount;
    private int taskCount;
    private int membershipCount;

    @PostConstruct
    public void init() {
        currentUser = userSession.getUser();
        calculateStatistics();
    }

    private void calculateStatistics() {
        if (currentUser != null && currentUser.getEntidad() != null) {
            // Contar proyectos creados
            List<Proyecto> createdProjects = currentUser.getEntidad().getProyectos();
            projectCount = createdProjects != null ? createdProjects.size() : 0;

            // Contar membresías (proyectos donde es miembro)
            List<Integrante> memberships = currentUser.getEntidad().getIntegrantes();
            membershipCount = memberships != null ? memberships.size() : 0;

            // Contar tareas asignadas a través de integrantes
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

    // Getters
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
}
