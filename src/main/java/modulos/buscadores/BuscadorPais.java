package modulos.buscadores;

import modulos.agregacion.entities.*;
import modulos.agregacion.repositories.IPaisRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class BuscadorPais {

    private final IPaisRepository repoPais;

    public BuscadorPais(IPaisRepository repoPais) {
        this.repoPais = repoPais;
    }

    public Pais buscarOCrear(String elemento) {

        Pais pais = this.buscar(elemento);
        if(pais == null){
            pais = new Pais();
            pais.setPais(elemento);
        }
        return pais;
    }

    public Pais buscar(String elemento) {
        return this.repoPais.findByNombreNormalizado(elemento).orElse(null);
    }
}
