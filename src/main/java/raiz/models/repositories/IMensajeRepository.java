package raiz.models.repositories;

import raiz.models.entities.Mensaje;

import java.util.List;

public interface IMensajeRepository {
    List<Mensaje> findAll();
    void save(Mensaje entidad);
    void delete(Mensaje entidad);
    Mensaje findById(Long id);
    long getProxId();
    void update(Mensaje entidad);
}
