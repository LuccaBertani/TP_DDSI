package modulos.agregacion.entities.DbMain.algoritmosConsenso;

import modulos.agregacion.entities.DbMain.Coleccion;
import modulos.agregacion.entities.DbEstatica.Dataset;
import modulos.agregacion.entities.DbMain.Fuente;
import modulos.agregacion.entities.DbMain.Hecho;
import modulos.agregacion.entities.DbMain.hechoRef.HechoRef;
import modulos.buscadores.BuscadorHecho;

import java.util.ArrayList;
import java.util.List;

public class AlgoritmoConsensoMayoriaSimple implements IAlgoritmoConsenso {

    // Mayoría simple: si al menos la mitad de las fuentes contienen el mismo hecho, se lo considera consensuado
    @Override
    public void ejecutarAlgoritmoConsenso(BuscadorHecho buscadorHecho, List<Dataset> datasets, Coleccion coleccion) {
        // primero busco los ids de los hechos ref estaticos, pq son los unicos que pueden ser consensuados
        List<HechoRef> hechosRefEstaticos = coleccion.getHechos().stream().
                filter(h->h.getKey().getFuente().equals(Fuente.ESTATICA)).
                toList();

        List<HechoRef> nuevosHechosConsensuados = new ArrayList<>();

        for (HechoRef hechoRef: hechosRefEstaticos){
            if (datasets.size()/2 <= buscadorHecho.findCantDatasetsHecho(hechoRef.getKey().getId())){
                //coleccion.getHechosConsensuados().add(hechoRef);
                nuevosHechosConsensuados.add(hechoRef);
            }
        }
        coleccion.setHechosConsensuados(nuevosHechosConsensuados);
    }
}
