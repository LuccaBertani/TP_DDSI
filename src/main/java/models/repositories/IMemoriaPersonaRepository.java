package models.repositories;

import models.entities.Coleccion;
import models.entities.personas.Persona;

import java.util.List;

public interface IMemoriaPersonaRepository {
    List<Persona> findAll();
    void save(Persona entidad);
    void delete(Persona entidad);
    Persona findById(Long id);
}
