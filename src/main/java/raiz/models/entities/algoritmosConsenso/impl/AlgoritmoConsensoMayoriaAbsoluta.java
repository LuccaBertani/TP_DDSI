package raiz.models.entities.algoritmosConsenso.impl;

import raiz.models.entities.Coleccion;
import raiz.models.entities.Hecho;
import raiz.models.entities.algoritmosConsenso.IAlgoritmoConsenso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlgoritmoConsensoMayoriaAbsoluta implements IAlgoritmoConsenso {
    private Coleccion coleccion;
    public AlgoritmoConsensoMayoriaAbsoluta(Coleccion coleccion){
        this.coleccion = coleccion;
    }

    // Absoluta: si todas las fuentes contienen el mismo hecho, se lo considera consensuado.
    @Override
    public void ejecutarAlgoritmoConsenso(List<String> fuentes) {
        //coleccion.getHechosConsensuados().addAll()
        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensudados = hechos.stream().filter(hecho->hecho.getDataSets().equals(fuentes)).toList();
        coleccion.getHechosConsensuados().addAll(hechosConsensudados);
    }
}
