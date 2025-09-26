package modulos.agregacion.entities.DbMain.algoritmosConsenso;

import modulos.agregacion.entities.DbMain.Coleccion;
import modulos.agregacion.entities.DbEstatica.Dataset;
import modulos.buscadores.BuscadorHecho;

import java.util.List;

public interface IAlgoritmoConsenso {
    void ejecutarAlgoritmoConsenso(BuscadorHecho buscadorHecho, List<Dataset> datasets, Coleccion coleccion);
}
