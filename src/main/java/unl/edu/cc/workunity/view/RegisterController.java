package unl.edu.cc.workunity.view;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import unl.edu.cc.workunity.business.SecurityFacade;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.faces.FacesUtil;

import java.io.Serializable;

@Named
@ViewScoped
public class RegisterController implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @NotEmpty
    @Size(min = 3, message = "Nombre de usuario muy corto")
    private String username;

    @NotNull
    @NotEmpty
    @Size(min = 8, message = "Contraseña muy corta")
    private String password;

    @NotNull
    @NotEmpty
    @Size(min = 8, message = "Confirma tu contraseña")
    private String confirmPassword;

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    private String phone;

    @NotNull
    @NotEmpty
    @Email(message = "Email no válido")
    private String email;

    @Inject
    private SecurityFacade securityFacade;

    public String register() {
        if (!password.equals(confirmPassword)) {
            FacesUtil.addErrorMessage("Error", "Las contraseñas no coinciden");
            return null;
        }

        try {
            User user = securityFacade.registerUserWithEntity(username, password, email, firstName, lastName, phone);

            FacesUtil.addSuccessMessageAndKeep("Registro exitoso",
                    "Usuario " + user.getName() + " creado correctamente. Por favor inicia sesión.");

            return "login?faces-redirect=true";

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error", "Error al registrar usuario: " + e.getMessage());
            return null;
        }
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
