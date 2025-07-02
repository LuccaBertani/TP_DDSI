package modulos.agregacion.repositories;

import modulos.fuentes.Dataset;

import java.util.List;

public interface IDatasetsRepository {
    List<Dataset> findAll();
    void save(Dataset entidad);
    Dataset findById(Long id);
    long getProxId();
}
