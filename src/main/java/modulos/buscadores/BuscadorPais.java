package modulos.buscadores;

import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.Pais;

import java.util.List;
import java.util.Optional;

public class BuscadorPais {
    public static Pais buscarOCrear(List<Hecho> fuenteDinamica, String elemento, List<Hecho> fuenteProxy, List<Hecho> fuenteEstatica) {

        Pais pais = BuscadorPais.buscar(fuenteDinamica,elemento,fuenteProxy,fuenteEstatica);
        if(pais == null){
            pais = new Pais();
            pais.setPais(elemento);
        }
        return pais;
    }

    public static Pais buscar(List<Hecho> fuenteDinamica, String elemento, List<Hecho> fuenteProxy, List<Hecho> fuenteEstatica) {
        Optional<Hecho> hecho2 = fuenteDinamica.stream().filter(h -> Normalizador.normalizarYComparar(h.getAtributosHecho().getUbicacion().getPais().getPais(), elemento)).findFirst();
        Pais pais;
        // Si el país no existe, se crea

        if (hecho2.isPresent()) {
            pais = hecho2.get().getAtributosHecho().getUbicacion().getPais();
        } else {
            Optional<Hecho> hecho3 = fuenteProxy.stream().filter(h -> Normalizador.normalizarYComparar(h.getAtributosHecho().getUbicacion().getPais().getPais(), elemento)).findFirst();
            if (hecho3.isPresent()) {
                pais = hecho3.get().getAtributosHecho().getUbicacion().getPais();
            } else {
                Optional<Hecho> hecho4 = fuenteEstatica.stream().filter(h -> Normalizador.normalizarYComparar(h.getAtributosHecho().getUbicacion().getPais().getPais(), elemento)).findFirst();
                if (hecho4.isPresent()) {
                    pais = hecho4.get().getAtributosHecho().getUbicacion().getPais();
                } else {
                    pais = null;
                }
            }
        }

        return pais;
    }
}
