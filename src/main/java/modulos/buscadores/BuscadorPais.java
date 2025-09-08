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
public class BuscadorPais implements CommandLineRunner {

    private final IPaisRepository repoPais;
    private final IProvinciaRepository repoProvincia;

    public BuscadorPais(IPaisRepository repoPais, IProvinciaRepository repoProvincia) {
        this.repoPais = repoPais;
        this.repoProvincia = repoProvincia;
    }

    public Pais buscar(String elemento) {
        return this.repoPais.findByNombreNormalizado(elemento).orElse(null);
    }

    public Pais buscar(Long id){
        return this.repoPais.findById(id).orElse(null);
    }

    @Override
    public void run(String... args) throws Exception {
        if (repoPais.count() > 0)
            return;
        List<PaisProvincias> paisesProvincias = Geocodificador.obtenerTodosLosPaises();

        for (PaisProvincias paisProvincias : paisesProvincias){
            this.repoPais.save(paisProvincias.getPais());
            this.repoProvincia.saveAll(paisProvincias.getProvincias());
        }
    }
}
