package raiz.models.entities.algoritmosConsenso.impl;

import raiz.models.entities.Coleccion;
import raiz.models.entities.Hecho;
import raiz.models.entities.Normalizador;
import raiz.models.entities.algoritmosConsenso.IAlgoritmoConsenso;

import java.util.ArrayList;
import java.util.List;

public class AlgoritmoConsensoMultiplesMenciones implements IAlgoritmoConsenso {
    private Coleccion coleccion;
    public AlgoritmoConsensoMultiplesMenciones(Coleccion coleccion){
        this.coleccion = coleccion;
    }


    // múltiples menciones: si al menos dos fuentes contienen un mismo hecho y ninguna
    // otra fuente contiene otro de igual título pero diferentes atributos, se lo considera consensuado
    @Override
    public void ejecutarAlgoritmoConsenso(List<String> fuentes) {

        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensuados = hechos.stream().filter(hecho->
                this.dosFuentesContienenHecho(hecho, fuentes) &&
                !this.existeHechoIgualTitulo(hecho, hechos, fuentes)).toList();

        coleccion.getHechosConsensuados().addAll(hechosConsensuados);
    }

    private boolean dosFuentesContienenHecho(Hecho hecho, List<String> fuentes){
        int cantFuentes = 0;
        List<String> fuentesHecho = hecho.getDataSets();
        for (int i = 0; i < fuentesHecho.size(); i++){
            if (fuentes.contains(fuentesHecho.get(i))){
                cantFuentes++;
                if (cantFuentes == 2){
                    return true;
                }
            }
        }
        return false;
    }

    // No hay necesidad de hacer chequeo de fuentes
    private boolean existeHechoIgualTitulo(Hecho hecho, List<Hecho> hechos, List<String> fuentes){

        Hecho hechoRep = hechos.stream().filter
                        (h-> Normalizador.normalizarYComparar(h.getTitulo(), hecho.getTitulo())).findFirst().orElse(null);

        return hechoRep!=null;
    }

}
