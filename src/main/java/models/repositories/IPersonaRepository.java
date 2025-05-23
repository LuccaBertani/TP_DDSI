package models.repositories;

import models.entities.Hecho;
import models.entities.SolicitudHecho;
import models.entities.personas.Usuario;

import java.util.List;

public interface IPersonaRepository {
    List<Usuario> findAll();
    void save(Usuario entidad);
    void delete(Usuario entidad);
    Usuario findById(Long id);
    long getProxId();
    void update(Usuario entidad);
}
