package models.repositories.impl;

import models.entities.SolicitudHecho;
import models.entities.personas.Usuario;
import models.repositories.ISolicitudAgregarHechoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoriaSolicitudAgregarHechoRepository implements ISolicitudAgregarHechoRepository {
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
    public long getProxId() {
        long id_aux = -1;
        for(SolicitudHecho solicitudHecho: solicitudesAgregarHecho){
            if(id_aux == -1){
                id_aux = solicitudHecho.getId();
            } else if (id_aux < solicitudHecho.getId()) {
                id_aux = solicitudHecho.getId();
            }
        }
        return id_aux + 1;
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
