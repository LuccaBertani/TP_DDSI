package modulos.agregacion.entities.DbMain.algoritmosConsenso;

import modulos.agregacion.entities.DbMain.Coleccion;
import modulos.agregacion.entities.DbEstatica.Dataset;

import java.util.List;

// TODO modificar algoritmos consenso
public interface IAlgoritmoConsenso {
    void ejecutarAlgoritmoConsenso(List<Dataset> fuentes, Coleccion coleccion);
}
