package models.repositories;

import models.entities.DatosPersonalesPublicador;
import models.entities.personas.Persona;

import java.util.List;

public interface IDatosPersonalesRepository {
    public void save(Persona persona);
    public List<DatosPersonalesPublicador> findAll();
    public DatosPersonalesPublicador findById(Long id);
    public void delete(Persona persona);
}
