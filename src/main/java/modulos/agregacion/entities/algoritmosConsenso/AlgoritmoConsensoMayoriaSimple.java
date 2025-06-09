package modulos.agregacion.entities.algoritmosConsenso;

import modulos.agregacion.entities.Coleccion;
import modulos.fuentes.Dataset;
import modulos.shared.Hecho;

import java.util.List;

public class AlgoritmoConsensoMayoriaSimple implements IAlgoritmoConsenso {
    private Coleccion coleccion;
    public AlgoritmoConsensoMayoriaSimple(Coleccion coleccion){
        this.coleccion = coleccion;
    }

    // Mayoría simple: si al menos la mitad de las fuentes contienen el mismo hecho, se lo considera consensuado
    @Override
    public void ejecutarAlgoritmoConsenso(List<Dataset> fuentes){
        //coleccion.getHechosConsensuados().addAll()
        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensudados = hechos.stream().filter(hecho->this.mitadDeFuentesContienenHecho(hecho, fuentes)).toList();
        coleccion.getHechosConsensuados().addAll(hechosConsensudados);
    }

    private boolean mitadDeFuentesContienenHecho(Hecho hecho, List<Dataset> fuentes) {
        return hecho.getDatasets().size() >= fuentes.size() / 2;
    }

}
