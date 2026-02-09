package unl.edu.cc.workunity.business.service.common;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.workunity.business.service.CrudGenericService;
import unl.edu.cc.workunity.domain.common.Tarea;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.List;

@Stateless
public class TaskRepository {

    @Inject
    private CrudGenericService crudService;

    public Tarea save(Tarea tarea) {
        if (tarea.getId() == null) {
            return crudService.create(tarea);
        } else {
            return crudService.update(tarea);
        }
    }

    public Tarea find(Long id) throws EntityNotFoundException {
        Tarea tarea = crudService.find(Tarea.class, id);
        if (tarea == null) {
            throw new EntityNotFoundException("Tarea no encontrada con [" + id + "]");
        }
        return tarea;
    }

    public List<Tarea> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM tarea", Tarea.class);
    }

    public List<Tarea> findByProject(Long proyectoId) {
        String query = "SELECT * FROM tarea WHERE proyecto_id = " + proyectoId;
        return crudService.findWithNativeQuery(query, Tarea.class);
    }

    public List<Tarea> findByIntegrante(Long integranteId) {
        String query = "SELECT * FROM tarea WHERE integrante_asignado_id = " + integranteId;
        return crudService.findWithNativeQuery(query, Tarea.class);
    }

    public void delete(Long id) throws EntityNotFoundException {
        crudService.delete(Tarea.class, id);
    }
}
