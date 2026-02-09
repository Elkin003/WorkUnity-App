package unl.edu.cc.workunity.business.service.common;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.workunity.business.service.CrudGenericService;
import unl.edu.cc.workunity.domain.common.Proyecto;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.List;

@Stateless
public class ProjectRepository {
    @Inject
    private CrudGenericService crudService;

    public Proyecto save(Proyecto proyecto) {
        if (proyecto.getId() == null) {
            return crudService.create(proyecto);
        } else {
            return crudService.update(proyecto);
        }
    }

    public Proyecto find(Long id) throws EntityNotFoundException {
        Proyecto proyecto = crudService.find(Proyecto.class, id);
        if (proyecto == null) {
            throw new EntityNotFoundException("Proyecto no encontrado con [" + id + "]");
        }
        return proyecto;
    }

    public List<Proyecto> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM proyecto", Proyecto.class);
    }

    public List<Proyecto> findByCreator(Long creadorId) {
        String query = "SELECT * FROM proyecto WHERE creador_id = " + creadorId;
        return crudService.findWithNativeQuery(query, Proyecto.class);
    }

    public void delete(Long id) throws EntityNotFoundException {
        crudService.delete(Proyecto.class, id);
    }
}
