package raiz.models.entities.buscadores;

import raiz.models.entities.Categoria;
import raiz.models.entities.Hecho;
import raiz.models.entities.Normalizador;

import java.util.List;
import java.util.Optional;

public class BuscadorCategoria {
    public static Categoria buscar(List<Hecho> hechos, String elemento){
        Optional<Hecho> hecho1 = hechos.stream().filter(h-> Normalizador.normalizarYComparar(h.getCategoria().getTitulo(), elemento)).findFirst();
        Categoria categoria;
        // Si la categoría no existe, se crea

        if (hecho1.isPresent()){
            categoria = hecho1.get().getCategoria();
        }
        else{
            categoria = new Categoria();
            categoria.setTitulo(elemento);
        }
        return categoria;
    }
}
