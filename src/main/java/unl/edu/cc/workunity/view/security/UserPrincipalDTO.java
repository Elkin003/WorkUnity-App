package unl.edu.cc.workunity.view.security;

import unl.edu.cc.workunity.domain.security.User;

import java.io.Serializable;

/**
 * DTO para representar el usuario principal en la sesión HTTP
 */
public class UserPrincipalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;

    public UserPrincipalDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }

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
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserPrincipalDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
