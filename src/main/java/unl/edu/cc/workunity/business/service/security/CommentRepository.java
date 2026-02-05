package unl.edu.cc.workunity.business.service.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.workunity.business.service.common.CrudGenericService;
import unl.edu.cc.workunity.domain.Comentario;
import unl.edu.cc.workunity.domain.Tarea;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para la gestión de Comentarios
 */
@Stateless
public class CommentRepository {
    @Inject
    private CrudGenericService crudService;
    @PersistenceContext
    private EntityManager em;




    public Comentario save(Comentario comentario) {
        if (comentario.getId() == null) {
            em.persist(comentario);
            return comentario;
        } else {
            return em.merge(comentario);
        }
    }

    public Comentario find(Long id) throws EntityNotFoundException {
        Comentario comentario = crudService.find(Comentario.class, id);
        if (comentario == null) {
            throw new EntityNotFoundException("Comentario no encontrado con [" + id + "]");
        }
        return comentario;
    }

    public List<Comentario> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM comentario", Comentario.class);
    }

    // Versión con objeto Tarea
    public List<Comentario> findByTask(Tarea tarea) {
        String query = "SELECT * FROM comentario WHERE tarea_id = " + tarea.getId();
        return crudService.findWithNativeQuery(query, Comentario.class);
    }

    // Versión con ID de Tarea (NUEVO)
    public List<Comentario> findByTask(Long tareaId) {
        String query = "SELECT * FROM comentario WHERE tarea_id = " + tareaId;
        return crudService.findWithNativeQuery(query, Comentario.class);
    }

    public void delete(Long id) throws EntityNotFoundException {
        Comentario comentario = find(id);
        em.remove(em.merge(comentario));
    }

}
