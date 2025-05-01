package models.repositories;

import models.entities.Coleccion;

import java.util.List;

public interface IColeccionRepository {
    public List<Coleccion> findAll();
    public void save(Coleccion hecho);
    public void delete(Coleccion hecho);
    public Coleccion findById(Long id);
}
