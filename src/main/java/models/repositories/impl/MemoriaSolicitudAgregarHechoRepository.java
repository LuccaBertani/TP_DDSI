package models.repositories.impl;

import models.entities.SolicitudHecho;
import models.repositories.IRepository;

import java.util.ArrayList;
import java.util.List;

public class MemoriaSolicitudAgregarHechoRepository implements IRepository<SolicitudHecho> {
    List<SolicitudHecho> solicitudesAgregarHecho;

    public MemoriaSolicitudAgregarHechoRepository(){
        this.solicitudesAgregarHecho = new ArrayList<>();
    }
//id del hecho
    @Override
    public SolicitudHecho findById(Long id){
        return this.solicitudesAgregarHecho.stream()
                .filter(solicitud -> solicitud.getHecho().getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public List<SolicitudHecho> findAll() {
        return this.solicitudesAgregarHecho;
    }

    @Override
    public void save(SolicitudHecho solicitud) {
        solicitudesAgregarHecho.add(solicitud);
    }

    @Override
    public void delete(SolicitudHecho solicitud) {
        this.solicitudesAgregarHecho.remove(solicitud);
    }
}
