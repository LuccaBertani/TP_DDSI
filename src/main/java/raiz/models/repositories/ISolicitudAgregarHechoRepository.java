package raiz.models.repositories;

import raiz.models.entities.SolicitudHecho;

import java.util.List;

public interface ISolicitudAgregarHechoRepository {
    List<SolicitudHecho> findAll();
    void save(SolicitudHecho entidad);
    void delete(SolicitudHecho entidad);
    SolicitudHecho findById(Long id);
    long getProxId();
    void update(SolicitudHecho entidad);
}