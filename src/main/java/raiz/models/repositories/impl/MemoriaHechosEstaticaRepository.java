package raiz.models.repositories.impl;

import org.springframework.stereotype.Repository;
import raiz.models.entities.Hecho;
import raiz.models.repositories.IHechosEstaticaRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoriaHechosEstaticaRepository implements IHechosEstaticaRepository {

    private List<Hecho> hechos;
    private List<Hecho> snapshotHechos;
    private List<String> datasets;

    public MemoriaHechosEstaticaRepository() {
        this.hechos = new ArrayList<>();
        this.snapshotHechos = new ArrayList<>();
        this.datasets = new ArrayList<>();
    }

    @Override
    public List<Hecho> findAll() {
        return hechos;
    }

    @Override
    public void save(Hecho entidad) {
        hechos.add(entidad);
    }

    @Override
    public void delete(Hecho entidad) {
        entidad.setActivo(false);
    }

    @Override
    public Hecho findById(Long id) {
        return this.hechos.stream()
                .filter(hecho -> hecho.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public long getProxId() {
        long id_aux = -1;
        for(Hecho hecho: hechos){
            if(id_aux == -1){
                id_aux = hecho.getId();
            } else if (id_aux < hecho.getId()) {
                id_aux = hecho.getId();
            }
        }
        return id_aux + 1;
    }

    @Override
    public void update(Hecho entidad) {
        this.hechos.remove(entidad);
        this.save(entidad);
    }

    @Override
    public List<Hecho> getSnapshotHechos() {
        return this.snapshotHechos;
    }

    @Override
    public void clearSnapshotHechos() {
        this.snapshotHechos.clear();
    }

    @Override
    public List<String> getDatasets(){
        return this.datasets;
    }
}
