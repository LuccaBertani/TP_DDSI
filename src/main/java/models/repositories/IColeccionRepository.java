package models.repositories;

import models.entities.Coleccion;
import models.entities.SolicitudHecho;

import java.util.List;

public interface IColeccionRepository {
    List<Coleccion> findAll();
    void save(Coleccion entidad);
    void delete(Coleccion entidad);
    Coleccion findById(Long id);
    long getProxId();
    void update(Coleccion entidad);
}
