package raiz.models.entities.algoritmosConsenso.impl;

import raiz.models.entities.Coleccion;
import raiz.models.entities.Hecho;
import raiz.models.entities.algoritmosConsenso.IAlgoritmoConsenso;

import java.util.List;

public class AlgoritmoConsensoMayoriaAbsoluta implements IAlgoritmoConsenso {
    private Coleccion coleccion;
    public AlgoritmoConsensoMayoriaAbsoluta(Coleccion coleccion){
        this.coleccion = coleccion;
    }

    // Absoluta: si todas las fuentes contienen el mismo, se lo considera consensuado.

    @Override
    public void ejecutarAlgoritmoConsenso(List<String> fuentes) {

        //coleccion.getHechosConsensuados().addAll()

    }
}
