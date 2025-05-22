package models.entities.fuentes;

import models.entities.Hecho;
import models.entities.ModificadorHechos;

import java.util.ArrayList;
import java.util.List;


public class FuenteDinamica implements Fuente {

    private Hecho hecho;

    // TODO en entrega 2. Mapeo de urls
    public ModificadorHechos leerFuente(List<Hecho> hechos){
        return new ModificadorHechos(new ArrayList<>(),new ArrayList<>());
    }
}
