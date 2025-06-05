package raiz.models.entities.fuentes;

import lombok.Getter;
import lombok.Setter;
import raiz.models.entities.Hecho;
import raiz.models.entities.LectorCSV;
import raiz.models.entities.ModificadorHechos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FuenteEstatica {
    private String dataSet;
    public List<Hecho> leerFuente(List<Hecho> hechosFuenteProxy, List<Hecho> hechosFuenteDinamica, List<Hecho> hechosFuenteEstatica, List<String> datasets){

        String[] nombreArchivo = this.dataSet.split("\\.");
        String formato = nombreArchivo[1].toLowerCase();
        if (formato.equals("csv")){
            var lectorCSV = new LectorCSV(this.dataSet);
            return lectorCSV.leerCSV(hechosFuenteProxy,hechosFuenteDinamica,hechosFuenteEstatica, datasets);
        }
        else if (formato.equals("json")){
            //TODO el lector del formato JSON
            List<Hecho>invento = new ArrayList<>();
            return invento;
        }

        return new ArrayList<>();

    }
}
