package models.repositories;

import models.entities.Coleccion;
import models.entities.Hecho;
import models.entities.SolicitudHecho;

import java.util.List;

public interface IMemoriaSolicitudAgregarHechoRepository {
    List<SolicitudHecho> findAll();
    void save(SolicitudHecho entidad);
    void delete(SolicitudHecho entidad);
    SolicitudHecho findById(Long id);
}
