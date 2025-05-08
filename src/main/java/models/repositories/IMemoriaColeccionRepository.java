package models.repositories;

import models.entities.Coleccion;

import java.util.List;

public interface IMemoriaColeccionRepository {
    List<Coleccion> findAll();
    void save(Coleccion entidad);
    void delete(Coleccion entidad);
    Coleccion findById(Long id);
}
