package modulos.buscadores;

import modulos.agregacion.entities.Pais;
import modulos.agregacion.entities.Provincia;
import modulos.agregacion.entities.Ubicacion;
import modulos.agregacion.repositories.IPaisRepository;
import modulos.agregacion.repositories.IProvinciaRepository;
import modulos.agregacion.repositories.IUbicacionRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BuscadorUbicacion {

    private final IUbicacionRepository repoUbicacion;

    public BuscadorUbicacion(IUbicacionRepository repoUbicacion) {
        this.repoUbicacion = repoUbicacion;
    }

    public Ubicacion buscar(Long id_pais, Long id_provincia) {

        return this.repoUbicacion.findByPaisIdAndProvinciaId(id_pais,id_provincia).orElse(null);
    }

    public Ubicacion buscarOCrear(Pais pais, Provincia provincia){

        Long id_pais;
        Long id_provincia;

        if(pais == null){
            id_pais = null;
        }
        else{
            id_pais = pais.getId();
        }
        if(provincia == null){
            id_provincia = null;
        }
        else {
            id_provincia = provincia.getId();
        }

        Ubicacion ubicacion = this.buscar(id_pais, id_provincia);

        if (ubicacion == null){
            if (pais != null && provincia != null){
                if (provincia.getPais().getId().equals(pais.getId())){
                    ubicacion = new Ubicacion(pais, provincia);
                    repoUbicacion.save(ubicacion);
                }
            }
            else{
                ubicacion = new Ubicacion(pais, provincia);
                repoUbicacion.save(ubicacion);
            }
        }

        return ubicacion;
    }
}
