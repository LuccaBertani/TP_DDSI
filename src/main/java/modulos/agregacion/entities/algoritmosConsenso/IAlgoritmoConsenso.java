package modulos.agregacion.entities.algoritmosConsenso;

import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.fuentes.Dataset;

import java.util.List;

public interface IAlgoritmoConsenso {
    void ejecutarAlgoritmoConsenso(List<Dataset> fuentes, Coleccion coleccion);
}
