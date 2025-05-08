package models.repositories;

import models.entities.Coleccion;
import models.entities.SolicitudHecho;

import java.util.List;

public interface IMemoriaSolicitudEliminarHechoRepository {
    List<SolicitudHecho> findAll();
    void save(SolicitudHecho entidad);
    void delete(SolicitudHecho entidad);
    SolicitudHecho findById(Long id);
}
