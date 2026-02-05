package unl.edu.cc.workunity.business.service.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.workunity.business.service.common.CrudGenericService;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;

/**
 * Repositorio en memoria para la gestión de usuarios (credenciales)
 */
@Stateless
public class UserRepository {


    @Inject
    private CrudGenericService crudService;
    @PersistenceContext
    private EntityManager em;



    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }

    /**
     * Busca un usuario por ID
     */
    public User find(Long id) throws EntityNotFoundException {
        User user = crudService.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID [" + id + "]");
        }
        return user;
    }

    /**
     * Busca un usuario por nombre de usuario
     */
    public User find(String name) throws EntityNotFoundException {
        String query = "SELECT * FROM user WHERE name = '" + name + "'";
        List<User> results = crudService.findWithNativeQuery(query, User.class);

        if (results == null || results.isEmpty()) {
            throw new EntityNotFoundException("Usuario no encontrado con nombre [" + name + "]");
        }

        return results.get(0);
    }

    /**
     * Retorna todos los usuarios
     */
    public List<User> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM user", User.class);
    }
}
