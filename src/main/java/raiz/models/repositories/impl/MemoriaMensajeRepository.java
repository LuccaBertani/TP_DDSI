package raiz.models.repositories.impl;

import org.springframework.stereotype.Repository;
import raiz.models.entities.Mensaje;
import raiz.models.repositories.IMensajeRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemoriaMensajeRepository implements IMensajeRepository {
    List<Mensaje> mensajes;

    public MemoriaMensajeRepository(){
        this.mensajes = new ArrayList<>();
    }

    @Override
    public Mensaje findById(Long id){
        return this.mensajes.stream()
                .filter(mensaje -> mensaje.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public long getProxId() {
        long id_aux = -1;
        for(Mensaje mensaje: mensajes){
            if(id_aux == -1){
                id_aux = mensaje.getId();
            } else if (id_aux < mensaje.getId()) {
                id_aux = mensaje.getId();
            }
        }
        return id_aux + 1;
    }

    @Override
    public List<Mensaje> findAll() {
        return this.mensajes;
    }


    @Override
    public void save(Mensaje mensaje) {
        this.mensajes.add(mensaje);
    }

    @Override
    public void delete(Mensaje mensaje) {
        this.mensajes.remove(mensaje);
    }

    @Override
    public void update(Mensaje mensaje){
        this.mensajes.remove(mensaje);
        this.save(mensaje);
    }
}
