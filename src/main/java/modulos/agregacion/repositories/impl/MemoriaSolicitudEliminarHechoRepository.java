package modulos.agregacion.repositories.impl;

import modulos.agregacion.repositories.IRepository;
import modulos.solicitudes.SolicitudHecho;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("SolicitudEliminarHechoRepository")
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
    public long getProxId() {
        long id_aux = -1;
        for(SolicitudHecho solicitudHecho: solicitudesEliminarHecho){
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
        return this.solicitudesEliminarHecho;
    }

    @Override
    public void save(SolicitudHecho solicitud) {
        solicitudesEliminarHecho.add(solicitud);
    }

    @Override
    public void delete(SolicitudHecho solicitud) {
        solicitud.setProcesada(true);
    }

    @Override
    public void update(SolicitudHecho entidad){
        this.delete(entidad);
        this.save(entidad);
    }
}
