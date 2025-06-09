package raiz.models.entities.algoritmosConsenso;

import raiz.models.entities.Dataset;

import java.util.List;

public interface IAlgoritmoConsenso {
    void ejecutarAlgoritmoConsenso(List<Dataset> fuentes);
}
