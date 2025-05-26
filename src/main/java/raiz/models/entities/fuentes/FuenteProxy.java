package raiz.models.entities.fuentes;

import raiz.models.entities.Hecho;
import raiz.models.entities.ModificadorHechos;

import java.util.ArrayList;
import java.util.List;

public class FuenteProxy implements Fuente {
    // TODO en entrega 2
    public ModificadorHechos leerFuente(List<Hecho> hechos){
        return new ModificadorHechos(new ArrayList<>(),new ArrayList<>());
    }
}
