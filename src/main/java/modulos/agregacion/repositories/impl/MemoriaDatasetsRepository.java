package modulos.agregacion.repositories.impl;

import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.repositories.IDatasetsRepository;
import modulos.fuentes.Dataset;

import java.util.ArrayList;
import java.util.List;

public class MemoriaDatasetsRepository implements IDatasetsRepository {
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
    public List<Dataset> findAll() {
        return this.datasets;
    }

    @Override
    public void save(Dataset dataset) {
        datasets.add(dataset);
    }


}
