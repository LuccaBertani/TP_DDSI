package models.repositories;

import models.entities.Coleccion;
import models.entities.personas.Usuario;

import java.util.List;

public interface IMemoriaPersonaRepository {
    List<Usuario> findAll();
    void save(Usuario entidad);
    void delete(Usuario entidad);
    Usuario findById(Long id);
}
