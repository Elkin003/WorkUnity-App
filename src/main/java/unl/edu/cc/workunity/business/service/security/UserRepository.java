package unl.edu.cc.workunity.business.service.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import unl.edu.cc.workunity.business.service.CrudGenericService;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;

@Stateless
public class UserRepository {

    @Inject
    private CrudGenericService crudService;

    public User save(User user) {
        if (user.getId() == null) {
            return crudService.create(user);
        } else {
            return crudService.update(user);
        }
    }

    public User find(@NotNull Long id) throws EntityNotFoundException {
        User user = crudService.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID [" + id + "]");
        }
        return user;
    }

    public User find(@NotNull String name) throws EntityNotFoundException {
        String query = "SELECT * FROM user_ WHERE name = '" + name + "'";
        User userFound = crudService.findSingleResultOrNullWithNativeQuery(query, User.class);
        if (userFound == null) {
            throw new EntityNotFoundException("Usuario no encontrado con nombre [" + name + "]");
        }
        return userFound;
    }

    public List<User> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM user_", User.class);
    }
}
