package models.repositories.impl;

import models.entities.personas.Usuario;
import models.repositories.IMemoriaPersonaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemoriaPersonaRepository implements IMemoriaPersonaRepository {

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
        return this.personas.stream().filter(persona -> persona.getDatosPersonales().getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(Usuario usuario){
        this.personas.remove(usuario);
    }
}
