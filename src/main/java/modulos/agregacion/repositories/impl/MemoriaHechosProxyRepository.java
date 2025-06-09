package modulos.agregacion.repositories.impl;
import modulos.shared.Hecho;
import org.springframework.stereotype.Repository;
import modulos.agregacion.repositories.IHechosProxyRepository;

import java.util.ArrayList;
import java.util.List;

//Maneja los datos en memoria
@Repository
public class MemoriaHechosProxyRepository implements IHechosProxyRepository {
    private List<Hecho> hechos;
    private List<Hecho> snapshotHechos;

    public MemoriaHechosProxyRepository(){
        this.hechos = new ArrayList<>();
        this.snapshotHechos = new ArrayList<>();
    }

    @Override
    public Hecho findById(Long id){
        return this.hechos.stream()
                .filter(hecho -> hecho.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public long getProxId() {
        long id_aux = -1;
        for(Hecho hecho: hechos){
            if(id_aux == -1){
                id_aux = hecho.getId();
            } else if (id_aux < hecho.getId()) {
                id_aux = hecho.getId();
            }
        }
        return id_aux + 1;
    }

    @Override
    public List<Hecho> findAll() {
        return this.hechos;
    }

    @Override
    public void save(Hecho hecho) {
        hechos.add(hecho);
    }

    @Override
    public void delete(Hecho hecho) {
        hecho.setActivo(false);
    }

    @Override
    public void update(Hecho entidad){
        this.hechos.remove(entidad);
        this.save(entidad);
    }

    @Override
    public List<Hecho> getSnapshotHechos(){
        return this.snapshotHechos;
    }

    @Override
    public void clearSnapshotHechos(){
        this.snapshotHechos.clear();
    }


}
