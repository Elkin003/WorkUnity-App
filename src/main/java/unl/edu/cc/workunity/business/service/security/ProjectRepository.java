package unl.edu.cc.workunity.business.service.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.workunity.business.service.common.CrudGenericService;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.Proyecto;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para la gestión de Proyectos
 */
@Stateless
public class ProjectRepository {
    @Inject
    private CrudGenericService crudService;
    @PersistenceContext
    private EntityManager em;

    public Proyecto save(Proyecto proyecto) {
        if (proyecto.getId() == null) {
            em.persist(proyecto);
            return proyecto;
        } else {
            return em.merge(proyecto);
        }
    }

    /**
     * Busca un proyecto por ID
     */
    public Proyecto find(Long id) throws EntityNotFoundException {
        Proyecto proyecto = crudService.find(Proyecto.class, id);
        if (proyecto == null) {
            throw new EntityNotFoundException("Proyecto no encontrado con [" + id + "]");
        }
        return proyecto;
    }

    /**
     * Retorna todos los proyectos
     */
    public List<Proyecto> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM proyecto", Proyecto.class);
    }

    /**
     * Busca proyectos por creador
     */
    public List<Proyecto> findByCreator(Long creadorId) {
        String query = "SELECT * FROM proyecto WHERE creador_id = " + creadorId;
        return crudService.findWithNativeQuery(query, Proyecto.class);
    }

    // Alternativa manteniendo la firma original
    public List<Proyecto> findByCreator(Entidad creador) {
        String query = "SELECT * FROM proyecto WHERE creador_id = " + creador.getId();
        return crudService.findWithNativeQuery(query, Proyecto.class);
    }

    public void delete(Long id) throws EntityNotFoundException {
        Proyecto proyecto = find(id);
        em.remove(em.merge(proyecto));
    }
}
