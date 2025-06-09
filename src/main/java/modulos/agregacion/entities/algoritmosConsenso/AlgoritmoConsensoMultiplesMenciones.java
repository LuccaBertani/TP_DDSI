package modulos.agregacion.entities.algoritmosConsenso;

import modulos.agregacion.entities.Coleccion;
import modulos.fuentes.Dataset;
import modulos.shared.Hecho;
import modulos.buscadores.Normalizador;

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
    public void ejecutarAlgoritmoConsenso(List<Dataset> fuentes) {

        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensuados = hechos.stream().filter(hecho->
                this.dosFuentesContienenHecho(hecho) &&
                !this.existeHechoIgualTituloDiferentesAtributos(hecho, hechos)).toList();

        coleccion.getHechosConsensuados().addAll(hechosConsensuados);
    }

    private boolean dosFuentesContienenHecho(Hecho hecho){
        return hecho.getDatasets().size() >= 2;
    }

    // No hay necesidad de hacer chequeo de fuentes
    private boolean existeHechoIgualTituloDiferentesAtributos(Hecho hecho, List<Hecho> hechos){
        Hecho hechoRep = hechos.stream().filter
                        (h-> Normalizador.normalizarYComparar(h.getTitulo(), hecho.getTitulo()) &&
                                this.tienenAtributosDistintos(hecho, h)).
                                findFirst().orElse(null);

        return hechoRep!=null;
    }

    private boolean tienenAtributosDistintos(Hecho h1, Hecho h2){
        return !Normalizador.normalizarYComparar(h1.getDescripcion(),h2.getDescripcion())||
                !h1.getCategoria().equals(h2.getCategoria()) ||
                !h1.getPais().equals(h2.getPais()) ||
                !h1.getFechaAcontecimiento().equals(h2.getFechaAcontecimiento());

    }

}
