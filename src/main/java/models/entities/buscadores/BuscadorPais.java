package models.entities.buscadores;

import models.entities.Hecho;
import models.entities.Normalizador;
import models.entities.Pais;

import java.util.List;
import java.util.Optional;

public class BuscadorPais {
    public static Pais buscar(List<Hecho> hechos, String elemento){
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
}
