package unl.edu.cc.workunity.business.service.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.workunity.business.service.common.CrudGenericService;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;

/**
 * Repositorio en memoria para la gestión de Entidades (perfiles de usuario)
 */
@Stateless
public class EntityRepository {
    @Inject
    private CrudGenericService crudService;
    @PersistenceContext
    private EntityManager em;

    public Entidad save(Entidad entidad) {
        if (entidad.getId() == null) {
            em.persist(entidad);
            return entidad;
        } else {
            return em.merge(entidad);
        }
    }
    /**
     * Busca una entidad por ID
     */
    public Entidad find(Long id) throws EntityNotFoundException {
        Entidad entidad = crudService.find(Entidad.class, id);
        if (entidad == null) {
            throw new EntityNotFoundException("Entidad no encontrada con [" + id + "]");
        }
        return entidad;
    }

    /**
     * Retorna todas las entidades
     */
    public List<Entidad> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM entidad", Entidad.class);
    }

    /**
     * Busca una entidad por usuario
     */
    public Entidad findByUser(Long usuarioId) throws EntityNotFoundException {
        String query = "SELECT * FROM entidad WHERE usuario_id = " + usuarioId;
        List<Entidad> results = crudService.findWithNativeQuery(query, Entidad.class);

        if (results == null || results.isEmpty()) {
            throw new EntityNotFoundException("Entidad no encontrada para el usuario con ID [" + usuarioId + "]");
        }

        return results.get(0);
    }

    // Alternativa si prefieres pasar el objeto User completo
    public Entidad findByUser(User usuario) throws EntityNotFoundException {
        String query = "SELECT * FROM entidad WHERE usuario_id = " + usuario.getId();
        List<Entidad> results = crudService.findWithNativeQuery(query, Entidad.class);

        if (results == null || results.isEmpty()) {
            throw new EntityNotFoundException("Entidad no encontrada para el usuario [" + usuario.getName() + "]");
        }

        return results.get(0);
    }

    public void delete(Long id) throws EntityNotFoundException {
        Entidad entidad = find(id);
        em.remove(em.merge(entidad));
    }
}

