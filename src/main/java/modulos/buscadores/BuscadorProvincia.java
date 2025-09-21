package modulos.buscadores;

import modulos.agregacion.entities.DbMain.Provincia;
import modulos.agregacion.repositories.IProvinciaRepository;
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

    public Provincia buscar(Long elemento) {
        return elemento != null ? repoProvincia.findById(elemento).orElse(null) : null;
    }

    public List<Provincia> buscarTodos() {
        System.out.println("ENTRO EN BUSCAR TODOS");

        return this.repoProvincia.findAll();
    }

}
