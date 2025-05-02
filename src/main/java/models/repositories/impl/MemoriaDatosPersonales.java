package models.repositories.impl;

import models.entities.DatosPersonalesPublicador;
import models.entities.personas.Persona;
import models.repositories.IDatosPersonalesRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class MemoriaDatosPersonales implements IDatosPersonalesRepository {

    private List<DatosPersonalesPublicador> datosPersonalesContribuyentes;

    @Override
    public void save(Persona persona) {
        this.datosPersonalesContribuyentes.add(persona.getDatosPersonales());
    }

    @Override
    public List<DatosPersonalesPublicador> findAll(){
        return this.datosPersonalesContribuyentes;
    }

    @Override
    public DatosPersonalesPublicador findById(Long id) {
        return this.datosPersonalesContribuyentes.stream().filter(persona -> persona.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(Persona persona){
        this.datosPersonalesContribuyentes.remove(persona.getDatosPersonales());
    }

}
