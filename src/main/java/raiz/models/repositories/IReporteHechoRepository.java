package raiz.models.repositories;

import raiz.models.entities.Mensaje;
import raiz.models.entities.Reporte;

import java.util.List;

public interface IReporteHechoRepository {
    List<Reporte> findAll();
    void save(Reporte entidad);
    void delete(Reporte entidad);
    Reporte findById(Long id);
    long getProxId();
    void update(Reporte entidad);
}
