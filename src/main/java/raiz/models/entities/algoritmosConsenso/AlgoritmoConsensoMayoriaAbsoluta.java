package raiz.models.entities.algoritmosConsenso;

import raiz.models.entities.Coleccion;
import raiz.models.entities.Dataset;
import raiz.models.entities.Hecho;

import java.util.List;

public class AlgoritmoConsensoMayoriaAbsoluta implements IAlgoritmoConsenso {
    private Coleccion coleccion;
    public AlgoritmoConsensoMayoriaAbsoluta(Coleccion coleccion){
        this.coleccion = coleccion;
    }

    // Absoluta: si todas las fuentes contienen el mismo hecho, se lo considera consensuado.
    @Override
    public void ejecutarAlgoritmoConsenso(List<Dataset> fuentes) {
        //coleccion.getHechosConsensuados().addAll()
        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensudados = hechos.stream().filter(hecho->hecho.getDatasets().equals(fuentes)).toList();
        coleccion.getHechosConsensuados().addAll(hechosConsensudados);
    }
}
