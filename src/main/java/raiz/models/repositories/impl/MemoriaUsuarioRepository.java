package raiz.models.repositories.impl;

import raiz.models.entities.personas.Usuario;
import org.springframework.stereotype.Repository;
import raiz.models.repositories.IUsuarioRepository;

import java.util.List;

@Repository
public class MemoriaUsuarioRepository implements IUsuarioRepository {

    private List<Usuario> personas;

    @Override
    public void save(Usuario usuario) {
        this.personas.add(usuario);
    }

    @Override
    public List<Usuario> findAll(){
        return this.personas;
    }

    @Override
    public Usuario findById(Long id) {
        return this.personas.stream().filter(persona -> persona.getId().equals(id)).findFirst().orElse(null);
    }
    @Override
    public long getProxId(){
        long id_aux = -1;
        for(Usuario persona: personas){
            if(id_aux == -1){
                id_aux = persona.getId();
            } else if (id_aux < persona.getId()) {
                id_aux = persona.getId();
            }
        }
        return id_aux + 1;
    }

    public void delete(Usuario entidad) {
        this.personas.remove(entidad);
    }

    @Override
    public void update(Usuario entidad) {
        this.delete(entidad);
        this.save(entidad);
    }

}
