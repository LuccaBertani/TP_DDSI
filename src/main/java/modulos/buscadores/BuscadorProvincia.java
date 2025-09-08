package modulos.buscadores;

import modulos.agregacion.entities.*;
import modulos.agregacion.repositories.IPaisRepository;
import modulos.agregacion.repositories.IProvinciaRepository;
import modulos.shared.utils.Geocodificador;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuscadorProvincia {

    private final IProvinciaRepository repoProvincia;

    public BuscadorProvincia(IProvinciaRepository repoProvincia) {
        this.repoProvincia = repoProvincia;
    }

    public Provincia buscar(String elemento) {
        return this.repoProvincia.findByNombreNormalizado(elemento).orElse(null);
    }

}
