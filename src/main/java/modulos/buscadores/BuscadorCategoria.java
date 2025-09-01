package modulos.buscadores;

import modulos.agregacion.entities.Categoria;
import modulos.agregacion.entities.Hecho;

import java.util.List;
import java.util.Optional;

public class BuscadorCategoria {
    public static Categoria buscarOCrear(List<Hecho> fuenteDinamica, String elemento, List<Hecho> fuenteProxy, List<Hecho> fuenteEstatica){
        Categoria categoria = BuscadorCategoria.buscar(fuenteDinamica,elemento,fuenteProxy,fuenteEstatica);
        if(categoria == null){
            categoria = new Categoria();
            categoria.setTitulo(elemento);
        }
        return categoria;
    }

    public static Categoria buscar(List<Hecho> fuenteDinamica, String elemento, List<Hecho> fuenteProxy, List<Hecho> fuenteEstatica) {
        Optional<Hecho> hecho2 = fuenteDinamica.stream().filter(h -> Normalizador.normalizarYComparar(h.getAtributosHecho().getCategoria().getTitulo(), elemento)).findFirst();
        Categoria categoria;
        // Si el país no existe, se crea

        if (hecho2.isPresent()) {
            categoria = hecho2.get().getAtributosHecho().getCategoria();
        } else {
            Optional<Hecho> hecho3 = fuenteProxy.stream().filter(h -> Normalizador.normalizarYComparar(h.getAtributosHecho().getCategoria().getTitulo(), elemento)).findFirst();
            if (hecho3.isPresent()) {
                categoria = hecho3.get().getAtributosHecho().getCategoria();
            } else {
                Optional<Hecho> hecho4 = fuenteEstatica.stream().filter(h -> Normalizador.normalizarYComparar(h.getAtributosHecho().getCategoria().getTitulo(), elemento)).findFirst();
                if (hecho4.isPresent()) {
                    categoria = hecho4.get().getAtributosHecho().getCategoria();
                } else {
                    categoria = null;
                }
            }
        }

        return categoria;
    }

}
