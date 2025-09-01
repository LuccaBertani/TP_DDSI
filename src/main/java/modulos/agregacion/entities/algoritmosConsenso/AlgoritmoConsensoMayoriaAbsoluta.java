package modulos.agregacion.entities.algoritmosConsenso;

import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.fuentes.Dataset;
import modulos.agregacion.entities.Hecho;

import java.util.List;

public class AlgoritmoConsensoMayoriaAbsoluta implements IAlgoritmoConsenso {


    // Absoluta: si todas las fuentes contienen el mismo hecho, se lo considera consensuado.

    @Override
    public void ejecutarAlgoritmoConsenso(List<Dataset> fuentes, Coleccion coleccion) {
        //coleccion.getHechosConsensuados().addAll()
        List<Hecho> hechos = coleccion.getHechos();
        List<Long> idsDatasets = this.mapearIdsDatasets(fuentes);
        List<Hecho> hechosConsensudados = hechos.stream().filter(
                hecho->!coleccion.getHechosConsensuados().contains(hecho) &&
                        this.mapearIdsDatasets(hecho.getDatasets()).equals(idsDatasets)
                )
                .toList();
        coleccion.getHechosConsensuados().addAll(hechosConsensudados);
    }

    private List<Long> mapearIdsDatasets(List<Dataset> fuentes){
        return fuentes.stream().map(Dataset::getId).toList();
    }
}
