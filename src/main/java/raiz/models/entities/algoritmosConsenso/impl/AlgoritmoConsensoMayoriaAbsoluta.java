package raiz.models.entities.algoritmosConsenso.impl;

import raiz.models.entities.Coleccion;
import raiz.models.entities.Hecho;
import raiz.models.entities.Origen;
import raiz.models.entities.algoritmosConsenso.IAlgoritmoConsenso;

import java.util.ArrayList;
import java.util.List;

public class AlgoritmoConsensoMayoriaAbsoluta implements IAlgoritmoConsenso {
    private Coleccion coleccion;
    public AlgoritmoConsensoMayoriaAbsoluta(Coleccion coleccion){
        this.coleccion = coleccion;
    }

    @Override
    public void ejecutarAlgoritmoConsenso(List<Hecho> hechos) {

        //coleccion.getHechosConsensuados().addAll()

    }
}
