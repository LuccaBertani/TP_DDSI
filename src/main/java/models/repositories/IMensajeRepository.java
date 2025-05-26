package models.repositories;

import models.entities.Coleccion;
import models.entities.Mensaje;

import java.util.List;

public interface IMensajeRepository {
    List<Mensaje> findAll();
    void save(Mensaje entidad);
    void delete(Mensaje entidad);
    Mensaje findById(Long id);
    long getProxId();
    void update(Mensaje entidad);
}