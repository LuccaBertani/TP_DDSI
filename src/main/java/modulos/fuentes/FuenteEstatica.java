package modulos.fuentes;

import lombok.Getter;
import lombok.Setter;
import modulos.shared.Hecho;
import modulos.agregacion.entities.ModificadorHechos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
public class FuenteEstatica {
    private String dataSet;
    public ModificadorHechos leerFuente(List<Hecho> hechosFuenteProxy, List<Hecho> hechosFuenteDinamica, List<Hecho> hechosFuenteEstatica){

        String[] nombreArchivo = this.dataSet.split("\\.");
        String formato = nombreArchivo[1].toLowerCase();
        if (formato.equals("csv")){
            var lectorCSV = new LectorCSV(this.dataSet);
            return lectorCSV.leerCSV(hechosFuenteProxy,hechosFuenteDinamica,hechosFuenteEstatica);
        }
        else if (formato.equals("json")){
            //TODO el lector del formato JSON
            return new ModificadorHechos(new ArrayList<>(), new HashSet<>());
        }

        return new ModificadorHechos(new ArrayList<>(), new HashSet<>());
    }
}
