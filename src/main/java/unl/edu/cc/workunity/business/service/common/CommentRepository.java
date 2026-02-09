package unl.edu.cc.workunity.business.service.common;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.workunity.business.service.CrudGenericService;
import unl.edu.cc.workunity.domain.common.Comentario;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.List;

@Stateless
public class CommentRepository {

    @Inject
    private CrudGenericService crudService;

    public Comentario save(Comentario comentario) {
        if (comentario.getId() == null) {
            return crudService.create(comentario);
        } else {
            return crudService.update(comentario);
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

    public List<Comentario> findByTask(Long tareaId) {
        String query = "SELECT * FROM comentario WHERE tarea_id = " + tareaId;
        return crudService.findWithNativeQuery(query, Comentario.class);
    }

    public void delete(Long id) throws EntityNotFoundException {
        crudService.delete(Comentario.class, id);
    }
}
