package models.repositories.impl;
import models.entities.Coleccion;
import models.entities.Hecho;
import models.repositories.IHechosRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

//Maneja los datos en memoria
@Repository
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

    @Override
    public void update(Hecho hecho) {
        this.delete(findById(hecho.getId()));
        this.save(hecho);
    }
}
