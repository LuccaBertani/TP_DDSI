package modulos.buscadores;

import modulos.agregacion.entities.DbMain.Categoria;
import modulos.agregacion.repositories.ICategoriaRepository;
import org.springframework.stereotype.Component;

@Component
public class BuscadorCategoria {

    private final ICategoriaRepository categoriaRepository;

    public BuscadorCategoria(ICategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria buscar(String elemento) {
        return this.categoriaRepository.findByNombreNormalizado(elemento).orElse(null);
    }

    public Categoria buscar(Long id){
        return this.categoriaRepository.findById(id).orElse(null);
    }



}