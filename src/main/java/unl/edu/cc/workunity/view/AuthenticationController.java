package unl.edu.cc.workunity.view;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import unl.edu.cc.workunity.business.SecurityFacade;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.CredentialInvalidException;
import unl.edu.cc.workunity.faces.FacesUtil;
import unl.edu.cc.workunity.view.security.UserPrincipalDTO;
import unl.edu.cc.workunity.view.security.UserSession;

import java.io.Serializable;
import java.util.logging.Logger;

@Named
@ViewScoped
public class AuthenticationController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(AuthenticationController.class.getName());

    @NotNull
    @NotEmpty
    @Size(min = 3, message = "Nombre de usuario muy corto")
    private String username;

    @NotNull
    @NotEmpty
    @Size(min = 8, message = "Contraseña muy corta")
    private String password;

    @Inject
    private SecurityFacade securityFacade;

    @Inject
    private UserSession userSession;

    public String login() {
        logger.info("Login attempt for user: " + username);

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            FacesUtil.addErrorMessage("Error", "Credenciales incorrectas");
            return null;
        }

        try {
            User user = securityFacade.authenticate(username, password);
            setHttpSession(user);
            FacesUtil.addSuccessMessageAndKeep("Bienvenido", user.getEntidad().getNombre());
            userSession.postLogin(user);

            return "projects?faces-redirect=true";

        } catch (CredentialInvalidException e) {
            FacesUtil.addErrorMessage("Error", "Credenciales incorrectas");
            return null;
        } catch (Exception e) {
            logger.severe("Error during login: " + e.getMessage());
            FacesUtil.addErrorMessage("Error", "Credenciales incorrectas");
            return null;
        }
    }

    private void setHttpSession(User user) {
        FacesContext context = FacesContext.getCurrentInstance();
        UserPrincipalDTO userPrincipal = new UserPrincipalDTO(user);
        context.getExternalContext().getSessionMap().put("user", userPrincipal);
    }

    public String logout() throws ServletException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().invalidateSession();
        FacesUtil.addSuccessMessageAndKeep("Hasta pronto", "");
        ((jakarta.servlet.http.HttpServletRequest) facesContext.getExternalContext().getRequest()).logout();
        return "index?faces-redirect=true";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
