package models.repositories;

import models.entities.Coleccion;
import models.entities.Hecho;

import java.util.List;


public interface IMemoriaHechosRepository {
    List<Hecho> findAll();
    void save(Hecho entidad);
    void delete(Hecho entidad);
    Hecho findById(Long id);
}
