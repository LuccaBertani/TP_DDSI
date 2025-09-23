package modulos.buscadores;

import modulos.agregacion.entities.DbMain.Pais;
import modulos.agregacion.entities.DbMain.PaisProvincias;
import modulos.agregacion.repositories.DbMain.IPaisRepository;
import modulos.agregacion.repositories.DbMain.IProvinciaRepository;
import modulos.shared.utils.Geocodificador;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

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
        return id != null ? this.repoPais.findById(id).orElse(null) : null;
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
