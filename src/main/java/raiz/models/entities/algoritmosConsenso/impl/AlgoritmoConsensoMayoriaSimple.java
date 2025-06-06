package raiz.models.entities.algoritmosConsenso.impl;

import raiz.models.entities.Coleccion;
import raiz.models.entities.Hecho;
import raiz.models.entities.algoritmosConsenso.IAlgoritmoConsenso;

import java.util.List;

public class AlgoritmoConsensoMayoriaSimple implements IAlgoritmoConsenso {
    private Coleccion coleccion;
    public AlgoritmoConsensoMayoriaSimple(Coleccion coleccion){
        this.coleccion = coleccion;
    }

    // Mayoría simple: si al menos la mitad de las fuentes contienen el mismo hecho, se lo considera consensuado
    @Override
    public void ejecutarAlgoritmoConsenso(List<String> fuentes){
        //coleccion.getHechosConsensuados().addAll()
        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensudados = hechos.stream().filter(hecho->this.mitadDeFuentesContienenHecho(hecho, fuentes)).toList();
        coleccion.getHechosConsensuados().addAll(hechosConsensudados);
    }

    private boolean mitadDeFuentesContienenHecho(Hecho hecho, List<String> fuentes){
        int cantFuentes = 0;
        List<String> fuentesHecho = hecho.getDataSets();
        for (int i = 0; i < fuentesHecho.size(); i++){
            if (fuentes.contains(fuentesHecho.get(i))){
                cantFuentes++;
                if (cantFuentes >= fuentesHecho.size() / 2){
                    return true;
                }
            }
        }
        return false;
    }

}
