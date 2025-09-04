package modulos.agregacion.entities.fuentes;

import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.HechoDinamica;
import modulos.agregacion.entities.HechoEstatica;
import modulos.agregacion.entities.HechoProxy;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FuenteEstatica {
    private Dataset dataSet;
    public List<Hecho> leerFuente(List<HechoProxy> hechosFuenteProxy, List<HechoDinamica> hechosFuenteDinamica, List<HechoEstatica> hechosFuenteEstatica){

        String[] nombreArchivo = this.dataSet.getFuente().split("\\.");
        String formato = nombreArchivo[1].toLowerCase();
        if (formato.equals("csv")){
            var lectorCSV = new LectorCSV(this.dataSet);
            return lectorCSV.leerCSV(hechosFuenteProxy,hechosFuenteDinamica,hechosFuenteEstatica);
        }
        else if (formato.equals("json")){
            //TODO el lector del formato JSON
            List<Hecho> lista = new ArrayList<>();
            return lista;
        }

        List<Hecho> lista = new ArrayList<>();
        return lista;
    }
}
