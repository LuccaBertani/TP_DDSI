package raiz.models.repositories.impl;

import raiz.models.entities.SolicitudHecho;
import org.springframework.stereotype.Repository;
import raiz.models.repositories.ISolicitudModificarHechoRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoriaSolicitudModificarHechoRepository implements ISolicitudModificarHechoRepository {
    List<SolicitudHecho> solicitudesModificarHecho;

    public MemoriaSolicitudModificarHechoRepository(){
        this.solicitudesModificarHecho = new ArrayList<>();
    }
    //id del hecho
    @Override
    public SolicitudHecho findById(Long id){
        return this.solicitudesModificarHecho.stream()
                .filter(solicitud -> solicitud.getHecho().getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public long getProxId() {
        long id_aux = -1;
        for(SolicitudHecho solicitudHecho: solicitudesModificarHecho){
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
        return this.solicitudesModificarHecho;
    }

    @Override
    public void save(SolicitudHecho solicitud) {
        solicitudesModificarHecho.add(solicitud);
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
