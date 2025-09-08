package modulos.buscadores;

import modulos.agregacion.entities.*;
import modulos.agregacion.repositories.ICategoriaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
public class BuscadorCategoria {

    private final ICategoriaRepository categoriaRepository;

    public BuscadorCategoria(ICategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria buscar(String elemento) {
        return this.categoriaRepository.findByNombreNormalizado(elemento).orElse(null);
    }

}
