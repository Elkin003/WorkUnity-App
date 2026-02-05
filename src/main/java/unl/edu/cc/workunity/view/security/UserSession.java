package unl.edu.cc.workunity.view.security;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotNull;
import unl.edu.cc.workunity.business.service.security.EntityRepository;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Bean de sesión para mantener información del usuario autenticado
 */
@Named
@SessionScoped
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UserSession.class.getName());

    @Inject
    private EntityRepository entityRepository;

    private User user;

    /**
     * Método llamado después del login para inicializar la sesión
     */
    public void postLogin(@NotNull User user) throws EntityNotFoundException {
        logger.info("User logged in: " + user.getName());
        this.user = user;

        // Cargar la entidad (perfil) del usuario si existe
        if (user.getEntidad() == null) {
            try {
                Entidad entidad = entityRepository.findByUser(user);
                user.setEntidad(entidad);
                logger.info("Entidad loaded for user: " + entidad.getFullName());
            } catch (EntityNotFoundException e) {
                logger.warning("No entity found for user: " + user.getName());
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
