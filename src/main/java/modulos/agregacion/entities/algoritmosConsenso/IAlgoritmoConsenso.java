package modulos.agregacion.entities.algoritmosConsenso;

import modulos.fuentes.Dataset;

import java.util.List;

public interface IAlgoritmoConsenso {
    void ejecutarAlgoritmoConsenso(List<Dataset> fuentes);
}
