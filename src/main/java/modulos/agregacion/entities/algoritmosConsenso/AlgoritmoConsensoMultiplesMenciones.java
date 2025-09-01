package modulos.agregacion.entities.algoritmosConsenso;

import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.fuentes.Dataset;
import modulos.agregacion.entities.Hecho;
import modulos.buscadores.Normalizador;

import java.util.List;

public class AlgoritmoConsensoMultiplesMenciones implements IAlgoritmoConsenso {

    // múltiples menciones: si al menos dos fuentes contienen un mismo hecho y ninguna
    // otra fuente contiene otro de igual título pero diferentes atributos, se lo considera consensuado

    // Por la forma en la que tenemos implementadas las fuentes no hay necesidad de usar la lista de fuentes "global"
    @Override
    public void ejecutarAlgoritmoConsenso(List<Dataset> fuentes, Coleccion coleccion) {

        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensuados = hechos.stream().filter(hecho->
                !coleccion.getHechosConsensuados().contains(hecho) &&
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
                        (h-> Normalizador.normalizarYComparar(h.getAtributosHecho().getTitulo(), hecho.getAtributosHecho().getTitulo()) &&
                                this.tienenAtributosDistintos(hecho, h)).
                                findFirst().orElse(null);

        return hechoRep!=null;
    }

    private boolean tienenAtributosDistintos(Hecho h1, Hecho h2){
        return !Normalizador.normalizarYComparar(h1.getAtributosHecho().getDescripcion(),h2.getAtributosHecho().getDescripcion())||
                !h1.getAtributosHecho().getCategoria().equals(h2.getAtributosHecho().getCategoria()) ||
                !h1.getAtributosHecho().getUbicacion().getPais().equals(h2.getAtributosHecho().getUbicacion().getPais()) ||
                !h1.getAtributosHecho().getFechaAcontecimiento().equals(h2.getAtributosHecho().getFechaAcontecimiento()) ||
                !h1.getAtributosHecho().getUbicacion().getProvincia().equals(h2.getAtributosHecho().getUbicacion().getProvincia());
    }
}
