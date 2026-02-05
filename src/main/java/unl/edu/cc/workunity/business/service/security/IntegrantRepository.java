package unl.edu.cc.workunity.business.service.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.workunity.business.service.common.CrudGenericService;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.Integrante;
import unl.edu.cc.workunity.domain.Proyecto;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para la gestión de Integrantes
 */
@Stateless
public class IntegrantRepository {

    @Inject
    private CrudGenericService crudService;
    @PersistenceContext
    private EntityManager em;

    public Integrante save(Integrante integrante) {
        if (integrante.getId() == null) {
            em.persist(integrante);
            return integrante;
        } else {
            return em.merge(integrante);
        }
    }

    /**
     * Busca un integrante por ID
     */
    public Integrante find(Long id) throws EntityNotFoundException {
        Integrante integrante = crudService.find(Integrante.class, id);
        if (integrante == null) {
            throw new EntityNotFoundException("Integrante no encontrado con [" + id + "]");
        }
        return integrante;
    }

    /**
     * Retorna todos los integrantes
     */
    public List<Integrante> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM integrante", Integrante.class);
    }

    /**
     * Busca integrantes de un proyecto
     */
    public List<Integrante> findByProject(Long proyectoId) {
        String query = "SELECT * FROM integrante WHERE proyecto_id = " + proyectoId;
        return crudService.findWithNativeQuery(query, Integrante.class);
    }

    // Alternativa manteniendo la firma original
    public List<Integrante> findByProject(Proyecto proyecto) {
        String query = "SELECT * FROM integrante WHERE proyecto_id = " + proyecto.getId();
        return crudService.findWithNativeQuery(query, Integrante.class);
    }

    /**
     * Busca integrantes asociados a una entidad
     */
    public List<Integrante> findByEntity(Long entidadId) {
        String query = "SELECT * FROM integrante WHERE entidad_id = " + entidadId;
        return crudService.findWithNativeQuery(query, Integrante.class);
    }

    // Alternativa manteniendo la firma original
    public List<Integrante> findByEntity(Entidad entidad) {
        String query = "SELECT * FROM integrante WHERE entidad_id = " + entidad.getId();
        return crudService.findWithNativeQuery(query, Integrante.class);
    }

    /**
     * Busca un integrante específico por proyecto y entidad
     */
    public Integrante findByProjectAndEntity(Proyecto proyecto, Entidad entidad) throws EntityNotFoundException {
        String query = "SELECT * FROM integrante WHERE proyecto_id = " + proyecto.getId() +
                " AND entidad_id = " + entidad.getId();
        List<Integrante> results = crudService.findWithNativeQuery(query, Integrante.class);

        if (results == null || results.isEmpty()) {
            throw new EntityNotFoundException(
                    "Integrante no encontrado para el proyecto [" + proyecto.getNombre() +
                            "] y entidad [" + entidad.getNombre() + "]");
        }

        return results.get(0);
    }

    // Alternativa con IDs
    public Integrante findByProjectAndEntity(Long proyectoId, Long entidadId) throws EntityNotFoundException {
        String query = "SELECT * FROM integrante WHERE proyecto_id = " + proyectoId +
                " AND entidad_id = " + entidadId;
        List<Integrante> results = crudService.findWithNativeQuery(query, Integrante.class);

        if (results == null || results.isEmpty()) {
            throw new EntityNotFoundException(
                    "Integrante no encontrado para el proyecto ID [" + proyectoId +
                            "] y entidad ID [" + entidadId + "]");
        }

        return results.get(0);
    }

    public void delete(Long id) throws EntityNotFoundException {
        Integrante integrante = find(id);
        em.remove(em.merge(integrante));
    }
}

