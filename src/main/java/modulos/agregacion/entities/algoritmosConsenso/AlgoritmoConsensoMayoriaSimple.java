package modulos.agregacion.entities.algoritmosConsenso;

import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.fuentes.Dataset;
import modulos.agregacion.entities.Hecho;

import java.util.List;

public class AlgoritmoConsensoMayoriaSimple implements IAlgoritmoConsenso {

    // Mayoría simple: si al menos la mitad de las fuentes contienen el mismo hecho, se lo considera consensuado
    @Override
    public void ejecutarAlgoritmoConsenso(List<Dataset> fuentes, Coleccion coleccion){
        //coleccion.getHechosConsensuados().addAll()
        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensudados = hechos.stream().filter(hecho->!coleccion.getHechosConsensuados().contains(hecho) &&
                this.mitadDeFuentesContienenHecho(hecho, fuentes)).toList();
        coleccion.getHechosConsensuados().addAll(hechosConsensudados);
    }

    private boolean mitadDeFuentesContienenHecho(Hecho hecho, List<Dataset> fuentes) {
        return hecho.getDatasets().size() >= fuentes.size() / 2;
    }

}
