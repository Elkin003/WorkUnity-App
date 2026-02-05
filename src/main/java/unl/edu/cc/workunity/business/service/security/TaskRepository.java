package unl.edu.cc.workunity.business.service.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.workunity.business.service.common.CrudGenericService;
import unl.edu.cc.workunity.domain.Integrante;
import unl.edu.cc.workunity.domain.Proyecto;
import unl.edu.cc.workunity.domain.Tarea;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para la gestión de Tareas
 */
@Stateless
public class TaskRepository {

    @Inject
    private CrudGenericService crudService;
    @PersistenceContext
    private EntityManager em;


    public Tarea save(Tarea tarea) {
        if (tarea.getId() == null) {
            em.persist(tarea);
            return tarea;
        } else {
            return em.merge(tarea);
        }
    }
    /**
     * Busca una tarea por ID
     */
    public Tarea find(Long id) throws EntityNotFoundException {
        Tarea tarea = crudService.find(Tarea.class, id);
        if (tarea == null) {
            throw new EntityNotFoundException("Tarea no encontrada con [" + id + "]");
        }
        return tarea;
    }

    /**
     * Retorna todas las tareas
     */
    public List<Tarea> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM tarea", Tarea.class);
    }

    /**
     * Busca tareas por proyecto
     */
    public List<Tarea> findByProject(Long proyectoId) {
        String query = "SELECT * FROM tarea WHERE proyecto_id = " + proyectoId;
        return crudService.findWithNativeQuery(query, Tarea.class);
    }

    // Alternativa manteniendo la firma original
    public List<Tarea> findByProject(Proyecto proyecto) {
        String query = "SELECT * FROM tarea WHERE proyecto_id = " + proyecto.getId();
        return crudService.findWithNativeQuery(query, Tarea.class);
    }

    /**
     * Busca tareas asignadas a un integrante
     */
    public List<Tarea> findByIntegrante(Long integranteId) {
        String query = "SELECT * FROM tarea WHERE integrante_asignado_id = " + integranteId;
        return crudService.findWithNativeQuery(query, Tarea.class);
    }

    // Alternativa manteniendo la firma original
    public List<Tarea> findByIntegrante(Integrante integrante) {
        String query = "SELECT * FROM tarea WHERE integrante_asignado_id = " + integrante.getId();
        return crudService.findWithNativeQuery(query, Tarea.class);
    }

    /**
     * Elimina una tarea
     */
    public void delete(Long id) throws EntityNotFoundException {
        Tarea tarea = find(id);
        em.remove(em.merge(tarea));
    }
}

