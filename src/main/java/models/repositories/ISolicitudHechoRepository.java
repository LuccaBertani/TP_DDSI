package models.repositories;

import models.entities.Hecho;
import models.entities.SolicitudHecho;

import java.util.List;

public interface ISolicitudHechoRepository {
    public List<SolicitudHecho> findAll();
    public void save(SolicitudHecho solicitud);
    public void delete(SolicitudHecho solicitud); // En el tp no se borran los hechos (duda)
    public SolicitudHecho findById(Long id);
}
