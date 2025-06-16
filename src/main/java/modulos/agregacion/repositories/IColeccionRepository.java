package modulos.agregacion.repositories;

import modulos.agregacion.entities.Coleccion;

import java.util.List;

public interface IColeccionRepository {
    List<Coleccion> findAll();
    void save(Coleccion entidad);
    void delete(Coleccion entidad);
    Coleccion findById(Long id);
    long getProxId();
    void update(Coleccion entidad);
}