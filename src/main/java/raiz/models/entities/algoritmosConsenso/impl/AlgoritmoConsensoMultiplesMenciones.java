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

    // Por la forma en la que tenemos implementadas las fuentes no hay necesidad de usar la lista de fuentes "global"
    @Override
    public void ejecutarAlgoritmoConsenso(List<String> fuentes) {

        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensuados = hechos.stream().filter(hecho->
                this.dosFuentesContienenHecho(hecho) &&
                !this.existeHechoIgualTitulo(hecho, hechos)).toList();

        coleccion.getHechosConsensuados().addAll(hechosConsensuados);
    }

    private boolean dosFuentesContienenHecho(Hecho hecho){
        return hecho.getDataSets().size() >= 2;
    }

    // No hay necesidad de hacer chequeo de fuentes
    private boolean existeHechoIgualTitulo(Hecho hecho, List<Hecho> hechos){
        Hecho hechoRep = hechos.stream().filter
                        (h-> Normalizador.normalizarYComparar(h.getTitulo(), hecho.getTitulo())).findFirst().orElse(null);

        return hechoRep!=null;
    }

}
