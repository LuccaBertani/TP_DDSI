package models.repositories;

import models.entities.Hecho;
import models.entities.SolicitudHecho;

import java.util.List;


public interface IHechosRepository {
    List<Hecho> findAll();
    void save(Hecho entidad);
    void delete(Hecho entidad);
    Hecho findById(Long id);
    long getProxId();
    void update(Hecho entidad);
}
