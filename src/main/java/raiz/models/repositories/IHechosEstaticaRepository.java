package raiz.models.repositories;

import raiz.models.entities.Hecho;

import java.util.List;

public interface IHechosEstaticaRepository {
    List<Hecho> findAll();
    void save(Hecho entidad);
    void delete(Hecho entidad);
    Hecho findById(Long id);
    long getProxId();
    void update(Hecho entidad);
    public List<Hecho> getSnapshotHechos();
    public void clearSnapshotHechos();
}
