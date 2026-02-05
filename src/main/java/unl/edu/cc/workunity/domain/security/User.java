package unl.edu.cc.workunity.domain.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import unl.edu.cc.workunity.domain.Entidad;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa las credenciales de autenticación de un usuario
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 5)
    private String name;

    @NotNull
    @NotEmpty
    private String password;

    @Email
    private String email;

    // Relación con el perfil del usuario
    private Entidad entidad;

    public User() {
    }

    public User(Long id, String name, String password) {
        this.id = id;
        this.setName(name);
        this.setPassword(password);
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Entidad getEntidad() {
        return entidad;
    }

    public void setEntidad(Entidad entidad) {
        this.entidad = entidad;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
