package models.repositories;

import models.entities.SolicitudHecho;

import java.util.List;

public interface ISolicitudModificarHechoRepository {
    List<SolicitudHecho> findAll();
    void save(SolicitudHecho entidad);
    void delete(SolicitudHecho entidad);
    SolicitudHecho findById(Long id);
    long getProxId();
    void update(SolicitudHecho entidad);
}