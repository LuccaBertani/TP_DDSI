package modulos.buscadores;

import modulos.agregacion.entities.*;
import modulos.agregacion.repositories.IPaisRepository;
import modulos.agregacion.repositories.IProvinciaRepository;
import modulos.shared.utils.Geocodificador;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BuscadorProvincia {

    private final IProvinciaRepository repoProvincia;

    public BuscadorProvincia(IProvinciaRepository repoProvincia) {
        this.repoProvincia = repoProvincia;
    }

    public Provincia buscar(String elemento) {
        return this.repoProvincia.findByNombreNormalizado(elemento).orElse(null);
    }

    public Provincia buscar(Long elemento) {
        System.out.println("ENTRO EN BUSCAR CON ID");

        Provincia provincia =  repoProvincia.findById(1L).orElse(null);
        if(provincia!=null) {
            System.out.println(provincia.getProvincia());
        }
        provincia = repoProvincia.findById(22L).orElse(null);
        if(provincia!=null) {
            System.out.println(provincia.getProvincia());
        }

        provincia = repoProvincia.findById(elemento).orElse(null);
        if(provincia == null){
            System.out.println("DEFINITIVAMENTE ME GUSTA LA MIERDA");
        }else {
            System.out.println(provincia.getProvincia());
        }
        return provincia;
        }

    public List<Provincia> buscarTodos() {
        System.out.println("ENTRO EN BUSCAR TODOS");

        return this.repoProvincia.findAll();
    }

}
