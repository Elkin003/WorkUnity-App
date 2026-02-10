package unl.edu.cc.workunity.business.service.common;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.workunity.business.service.CrudGenericService;
import unl.edu.cc.workunity.domain.common.Integrante;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.List;

@Stateless
public class IntegrantRepository {

    @Inject
    private CrudGenericService crudService;

    public Integrante save(Integrante integrante) {
        if (integrante.getId() == null) {
            return crudService.create(integrante);
        } else {
            return crudService.update(integrante);
        }
    }

    public Integrante find(Long id) throws EntityNotFoundException {
        Integrante integrante = crudService.find(Integrante.class, id);
        if (integrante == null) {
            throw new EntityNotFoundException("Integrante no encontrado con [" + id + "]");
        }
        return integrante;
    }

    public List<Integrante> findAll() {
        return crudService.findWithNativeQuery("SELECT * FROM integrante", Integrante.class);
    }

    public List<Integrante> findByProject(Long proyectoId) {
        String query = "SELECT * FROM integrante WHERE proyecto_id = " + proyectoId;
        return crudService.findWithNativeQuery(query, Integrante.class);
    }

    public List<Integrante> findByEntity(Long entidadId) {
        String query = "SELECT * FROM integrante WHERE entidad_id = " + entidadId;
        return crudService.findWithNativeQuery(query, Integrante.class);
    }

    public Integrante findByProjectAndEntity(Long proyectoId, Long entidadId) throws EntityNotFoundException {
        String query = "SELECT * FROM integrante WHERE proyecto_id = " + proyectoId +
                " AND entidad_id = " + entidadId;
        Integrante result = crudService.findSingleResultOrNullWithNativeQuery(query, Integrante.class);

        if (result == null) {
            throw new EntityNotFoundException(
                    "Integrante no encontrado para proyecto ID [" + proyectoId +
                            "] y entidad ID [" + entidadId + "]");
        }

        return result;
    }

    public void delete(Long id) throws EntityNotFoundException {
        crudService.delete(Integrante.class, id);
    }

    public void deleteNative(Long id) {
        String query = "DELETE FROM integrante WHERE id = " + id;
        crudService.updateWithNativeQuery(query, new java.util.HashMap<>());
    }
}
