package models.entities.fuentes;

import models.entities.ModificadorHechos;
import models.entities.Hecho;

import java.util.List;

public interface Fuente {
    public ModificadorHechos leerFuente(List<Hecho> hechos);
}