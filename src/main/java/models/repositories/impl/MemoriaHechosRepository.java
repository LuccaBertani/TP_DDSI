package models.repositories.impl;
import models.entities.Hecho;
import models.repositories.IHechosRepository;

import java.util.ArrayList;
import java.util.List;

//Maneja los datos en memoria
public class MemoriaHechosRepository implements IHechosRepository {
    private List<Hecho> hechos;

    public MemoriaHechosRepository(){
        this.hechos = new ArrayList<>();
    }

    @Override
    public Hecho findById(Long id){
        return this.hechos.stream()
                .filter(hecho -> hecho.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public List<Hecho> findAll() {
        return this.hechos;
    }

    @Override
    public void save(Hecho hecho) {
        hechos.add(hecho);
    }

    @Override
    public void delete(Hecho hecho) {
    hecho.setActivo(false);
    }
}
