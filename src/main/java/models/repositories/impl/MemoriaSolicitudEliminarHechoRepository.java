package models.repositories.impl;

import models.entities.SolicitudHecho;
import models.repositories.IRepository;

import java.util.ArrayList;
import java.util.List;

public class MemoriaSolicitudEliminarHechoRepository implements IRepository<SolicitudHecho> {
    List<SolicitudHecho> solicitudesEliminarHecho;

    public MemoriaSolicitudEliminarHechoRepository(){
        this.solicitudesEliminarHecho = new ArrayList<>();
    }
    //id del hecho
    @Override
    public SolicitudHecho findById(Long id){
        return this.solicitudesEliminarHecho.stream()
                .filter(solicitud -> solicitud.getHecho().getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public List<SolicitudHecho> findAll() {
        return this.solicitudesEliminarHecho;
    }

    @Override
    public void save(SolicitudHecho solicitud) {
        solicitudesEliminarHecho.add(solicitud);
    }

    @Override
    public void delete(SolicitudHecho solicitud) {
        this.solicitudesEliminarHecho.remove(solicitud);
    }
}
