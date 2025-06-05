package raiz.models.entities.algoritmosConsenso.impl;

import raiz.models.entities.Coleccion;
import raiz.models.entities.Hecho;
import raiz.models.entities.algoritmosConsenso.IAlgoritmoConsenso;

import java.util.ArrayList;
import java.util.List;

public class AlgoritmoConsensoMayoriaSimple implements IAlgoritmoConsenso {
    private Coleccion coleccion;
    public AlgoritmoConsensoMayoriaSimple(Coleccion coleccion){
        this.coleccion = coleccion;
    }
    @Override
    public void ejecutarAlgoritmoConsenso(List<Hecho> hechos) {
        //coleccion.getHechosConsensuados().addAll()

    }
}
