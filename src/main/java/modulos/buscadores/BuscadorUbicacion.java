package modulos.buscadores;

import modulos.agregacion.entities.Pais;
import modulos.agregacion.entities.Provincia;
import modulos.agregacion.entities.Ubicacion;
import modulos.agregacion.repositories.IPaisRepository;
import modulos.agregacion.repositories.IProvinciaRepository;
import modulos.agregacion.repositories.IUbicacionRepository;
import org.springframework.stereotype.Component;

@Component
public class BuscadorUbicacion {

    private final IUbicacionRepository repoUbicacion;

    public BuscadorUbicacion(IUbicacionRepository repoUbicacion) {
        this.repoUbicacion = repoUbicacion;
    }

    public Ubicacion buscar(Pais pais, Provincia provincia) {
        return this.repoUbicacion.findByPaisIdAndProvinciaId(pais.getId(),provincia.getId()).orElse(null);
    }

    public Ubicacion buscarOCrear(Pais pais, Provincia provincia){
        Ubicacion ubicacion = this.buscar(pais, provincia);
        if(ubicacion == null){
            ubicacion = new Ubicacion(pais, provincia);
            repoUbicacion.save(ubicacion);
        }
        return ubicacion;
    }
}
