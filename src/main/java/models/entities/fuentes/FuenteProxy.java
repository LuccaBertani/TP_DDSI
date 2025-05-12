package models.entities.fuentes;

import models.entities.Hecho;
import models.entities.ModificadorHechos;

import java.util.ArrayList;
import java.util.List;

public class FuenteProxy implements Fuente {
    // TODO en entrega 2
    public ModificadorHechos leerFuente(List<Hecho> hechos){
        return new ModificadorHechos(new ArrayList<>(),new ArrayList<>());
    }
}
