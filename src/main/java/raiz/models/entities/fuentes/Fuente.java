package raiz.models.entities.fuentes;

import raiz.models.entities.ModificadorHechos;
import raiz.models.entities.Hecho;

import java.util.List;

public interface Fuente {
    public ModificadorHechos leerFuente(List<Hecho> hechos);
}