package raiz.models.repositories;

import raiz.models.entities.personas.Usuario;

import java.util.List;

public interface IUsuarioRepository {
    List<Usuario> findAll();
    void save(Usuario entidad);
    void delete(Usuario entidad);
    Usuario findById(Long id);
    long getProxId();
    void update(Usuario entidad);
}