package raiz.models.entities.buscadores;

import raiz.models.entities.Hecho;
import raiz.models.entities.Normalizador;
import raiz.models.entities.Pais;

import java.util.List;
import java.util.Optional;

public class BuscadorPais {
    public static Pais buscarOCrear(List<Hecho> hechos, String elemento){
        Optional<Hecho> hecho2 = hechos.stream().filter(h-> Normalizador.normalizarYComparar(h.getPais().getPais(), elemento)).findFirst();
        Pais pais;
        // Si el país no existe, se crea

        if (hecho2.isPresent()){
            pais = hecho2.get().getPais();
        } else{
            pais = new Pais();
            pais.setPais(elemento);
        }
        return pais;
    }
    
    public static Pais buscar(List<Hecho> hechos, String elemento){
        Optional<Hecho> hecho2 = hechos.stream().filter(h-> Normalizador.normalizarYComparar(h.getPais().getPais(), elemento)).findFirst();

        if (hecho2.isPresent()){
            return hecho2.get().getPais();
        }
        return null;
    }
    
}
