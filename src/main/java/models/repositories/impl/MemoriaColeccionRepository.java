package models.repositories.impl;

import models.entities.Coleccion;
import models.repositories.IMemoriaColeccionRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoriaColeccionRepository implements IMemoriaColeccionRepository {
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
}
