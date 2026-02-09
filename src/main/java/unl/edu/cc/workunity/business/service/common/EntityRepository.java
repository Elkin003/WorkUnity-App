package unl.edu.cc.workunity.business.service.common;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import unl.edu.cc.workunity.business.service.CrudGenericService;
import unl.edu.cc.workunity.domain.common.Entidad;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.List;

@ApplicationScoped
@Transactional
public class EntityRepository {

    @Inject
    private CrudGenericService crudService;

    public Entidad save(Entidad entidad) {
        if (entidad.getId() == null) {
            return crudService.create(entidad);
        } else {
            return crudService.update(entidad);
        }
    }

    public Entidad find(Long id) throws EntityNotFoundException {
        Entidad entidad = crudService.find(Entidad.class, id);
        if (entidad == null) {
            throw new EntityNotFoundException("Entidad no encontrada con [" + id + "]");
        }
        return entidad;
    }

    public List<Entidad> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM entidad", Entidad.class);
    }

    public Entidad findByUser(Long usuarioId) throws EntityNotFoundException {
        String query = "SELECT * FROM entidad WHERE usuario_id = " + usuarioId;
        Entidad result = crudService.findSingleResultOrNullWithNativeQuery(query, Entidad.class);

        if (result == null) {
            throw new EntityNotFoundException("Entidad no encontrada para el usuario con ID [" + usuarioId + "]");
        }

        return result;
    }

    public void delete(Long id) throws EntityNotFoundException {
        crudService.delete(Entidad.class, id);
    }
}
