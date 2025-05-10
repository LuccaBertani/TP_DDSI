package models.repositories.impl;

import models.entities.Coleccion;
import models.repositories.IColeccionRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoriaColeccionRepository implements IColeccionRepository {
    List <Coleccion> colecciones;

    public MemoriaColeccionRepository(){
        this.colecciones = new ArrayList<>();
    }

    @Override
    public Coleccion findById(Long id){
        return this.colecciones.stream()
                .filter(coleccion -> coleccion.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public long getProxId() {
        long id_aux = -1;
        for(Coleccion coleccion: colecciones){
            if(id_aux == -1){
                id_aux = coleccion.getId();
            } else if (id_aux < coleccion.getId()) {
                id_aux = coleccion.getId();
            }
        }
        return id_aux + 1;
    }

    @Override
    public List<Coleccion> findAll() {
        return this.colecciones;
    }


    @Override
    public void save(Coleccion coleccion) {
        colecciones.add(coleccion);
    }

    @Override
    public void delete(Coleccion coleccion) {
        coleccion.setActivo(false);
    }

    public void update(Coleccion coleccion){
        //TODO actualizar datos (igual para todos los repos)
    }
}
