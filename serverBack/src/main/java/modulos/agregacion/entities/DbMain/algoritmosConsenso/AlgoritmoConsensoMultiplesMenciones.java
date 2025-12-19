package modulos.agregacion.entities.DbMain.algoritmosConsenso;

import modulos.agregacion.entities.DbMain.Fuente;
import modulos.agregacion.entities.DbMain.hechoRef.HechoRef;
import modulos.agregacion.entities.DbMain.Coleccion;
import modulos.agregacion.entities.DbEstatica.Dataset;
import modulos.buscadores.BuscadorHecho;
import java.util.ArrayList;
import java.util.List;

public class AlgoritmoConsensoMultiplesMenciones implements IAlgoritmoConsenso {

    /* múltiples menciones: si al menos dos fuentes contienen un mismo hecho y ninguna
       otra fuente contiene otro de igual título pero diferentes atributos, se lo considera consensuado */

    @Override
    public void ejecutarAlgoritmoConsenso(BuscadorHecho buscadorHecho, List<Dataset> datasets, Coleccion coleccion) {

        List<HechoRef> hechosRefEstaticos = coleccion.getHechos().stream().
                filter(h->h.getKey().getFuente().equals(Fuente.ESTATICA)).
                toList();

        List<HechoRef> nuevosHechosConsensuados = new ArrayList<>();

        for (HechoRef hechoRef: hechosRefEstaticos) {


            if (buscadorHecho.findCantDatasetsHecho(hechoRef.getKey().getId()) >= 2 &&
            buscadorHecho.findCantHechosIgualTituloDiferentesAtributos(hechoRef.getKey().getId()) == 0) {

                nuevosHechosConsensuados.add(hechoRef);
            }
        }
        coleccion.setHechosConsensuados(nuevosHechosConsensuados);
    }
}