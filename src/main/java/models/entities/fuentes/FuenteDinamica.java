package models.entities.fuentes;

import models.entities.Globales;
import models.entities.Hecho;
import models.entities.ModificadorHechos;
import models.entities.Origen;

import java.util.ArrayList;
import java.util.List;


public class FuenteDinamica implements Fuente {
    // TODO en entrega 2. Mapeo de urls
    public ModificadorHechos leerFuente(List<Hecho> hechos){
        return new ModificadorHechos(new ArrayList<>(),new ArrayList<>());
    }
}
