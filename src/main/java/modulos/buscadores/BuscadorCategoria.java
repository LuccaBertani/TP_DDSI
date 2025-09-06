package modulos.buscadores;

import modulos.agregacion.entities.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class BuscadorCategoria {
    public static Categoria buscarOCrear(List<HechoDinamica> fuenteDinamica, String elemento, List<HechoProxy> fuenteProxy, List<HechoEstatica> fuenteEstatica){
        Categoria categoria = BuscadorCategoria.buscar(fuenteDinamica,elemento,fuenteProxy,fuenteEstatica);
        if(categoria == null){
            categoria = new Categoria();
            categoria.setTitulo(elemento);
        }
        return categoria;
    }

    public static Categoria buscar(List<HechoDinamica> fuenteDinamica, String elemento, List<HechoProxy> fuenteProxy, List<HechoEstatica> fuenteEstatica) {

        List<Hecho> hechos = new ArrayList<>();
        hechos.addAll(fuenteDinamica);
        hechos.addAll(fuenteEstatica);
        hechos.addAll(fuenteProxy);

        HashSet<Categoria> categorias = new HashSet<>();

        for (Hecho h : hechos){
            categorias.add(h.getAtributosHecho().getCategoria());
        }

        Boolean categoriaEquivalenteEncontrada = false;

        for (Categoria categoria : categorias){

            categoriaEquivalenteEncontrada = Normalizador.normalizarYComparar(categoria.getTitulo(), elemento);
            if (categoriaEquivalenteEncontrada)
                return categoria;
            else{
                List<Sinonimo> sinonimos = categoria.getSinonimos();
                for (Sinonimo sinonimo : sinonimos){
                    categoriaEquivalenteEncontrada = Normalizador.normalizarYComparar(sinonimo.getSinonimoStr(), elemento);
                    if (categoriaEquivalenteEncontrada)
                        return categoria;
                }
            }
        }
        return null;
    }

}
