package modulos.agregacion.repositories.impl;

import modulos.agregacion.entities.Coleccion;
import org.springframework.stereotype.Repository;
import modulos.agregacion.repositories.IColeccionRepository;

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
        Coleccion busqueda = this.findById(coleccion.getId());
        busqueda.setActivo(false);
    }

    @Override
    public void update(Coleccion entidad){
       this.colecciones.remove(entidad);
       this.save(entidad);
    }

}
