package models.repositories.impl;

import models.entities.SolicitudHecho;
import models.repositories.IMemoriaSolicitudEliminarHechoRepository;
import models.repositories.IRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoriaSolicitudEliminarHechoRepository implements IMemoriaSolicitudEliminarHechoRepository {
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
