package modulos.agregacion.repositories.impl;

import modulos.solicitudes.Reporte;
import modulos.agregacion.repositories.IReporteHechoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

//TODO hay un tema con la sincronizacion del id obtenido, puede que obtengan el mismo id dos hilos que llaman al mismo tiempo a getProxId()
@Repository
public class MemoriaReporteHechoRepository implements IReporteHechoRepository {
    List<Reporte> reportes;

    public MemoriaReporteHechoRepository(){
        this.reportes = new ArrayList<>();
    }

    @Override
    public Reporte findById(Long id){
        return this.reportes.stream()
                .filter(reporte -> reporte.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public long getProxId() {
        long id_aux = -1;
        for(Reporte reporte: reportes){
            if(id_aux == -1){
                id_aux = reporte.getId();
            } else if (id_aux < reporte.getId()) {
                id_aux = reporte.getId();
            }
        }
        return id_aux + 1;
    }

    @Override
    public List<Reporte> findAll() {
        return this.reportes;
    }


    @Override
    public void save(Reporte reporte) {
        this.reportes.add(reporte);
    }

    @Override
    public void delete(Reporte reporte) {
        this.reportes.remove(reporte);
    }

    @Override
    public void update(Reporte reporte){
        this.reportes.remove(reporte);
        this.save(reporte);
    }
}
