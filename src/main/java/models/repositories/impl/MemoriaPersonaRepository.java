package models.repositories.impl;

import models.entities.personas.Persona;
import models.repositories.IRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemoriaPersonaRepository implements IRepository<Persona> {

    private List<Persona> personas;

    @Override
    public void save(Persona persona) {
        this.personas.add(persona);
    }

    @Override
    public List<Persona> findAll(){
        return this.personas;
    }

    @Override
    public Persona findById(Long id) {
        return this.personas.stream().filter(persona -> persona.getDatosPersonales().getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(Persona persona){
        this.personas.remove(persona);
    }

}
