package modulos.agregacion.repositories;

import modulos.solicitudes.SolicitudHecho;

import java.util.List;

public interface ISolicitudAgregarHechoRepository {
    List<SolicitudHecho> findAll();
    void save(SolicitudHecho entidad);
    void delete(SolicitudHecho entidad);
    SolicitudHecho findById(Long id);
    long getProxId();
    void update(SolicitudHecho entidad);
}