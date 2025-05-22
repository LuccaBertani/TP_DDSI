package models.repositories.impl;

import models.entities.personas.Usuario;
import models.repositories.IPersonaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemoriaPersonaRepository implements IPersonaRepository {

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

    @Override
    public void update(Usuario usuario) {
        this.delete(usuario);
        this.save(usuario);
    }

    @Override
    public void delete(Usuario usuario){
        this.personas.remove(usuario);
    }
}
