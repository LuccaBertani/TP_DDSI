package modulos.agregacion.repositories.impl;

import modulos.agregacion.repositories.IRepository;
import modulos.fuentes.Dataset;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoriaDatasetsRepository implements IRepository<Dataset> {
    List<Dataset> datasets;

    public MemoriaDatasetsRepository(){
        this.datasets = new ArrayList<>();
    }

    @Override
    public Dataset findById(Long id){
        return this.datasets.stream()
                .filter(dataset -> dataset.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public long getProxId() {
        long id_aux = -1;
        for(Dataset dataset: datasets){
            if(id_aux == -1){
                id_aux = dataset.getId();
            } else if (id_aux < dataset.getId()) {
                id_aux = dataset.getId();
            }
        }
        return id_aux + 1;
    }

    @Override
    public void update(Dataset entidad) {

    }

    @Override
    public List<Dataset> findAll() {
        return this.datasets;
    }

    @Override
    public void save(Dataset dataset) {
        datasets.add(dataset);
    }

    @Override
    public void delete(Dataset entidad) {

    }


}
