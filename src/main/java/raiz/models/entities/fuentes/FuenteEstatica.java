package raiz.models.entities.fuentes;

import lombok.Getter;
import lombok.Setter;
import raiz.models.entities.Hecho;
import raiz.models.entities.LectorCSV;
import raiz.models.entities.ModificadorHechos;

import java.util.ArrayList;
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
            List<Hecho>invento = new ArrayList<>();
            ModificadorHechos modificadorHechos = new ModificadorHechos(new ArrayList<>(), new ArrayList<>());
            return modificadorHechos;
        }


        ModificadorHechos modificadorHechos = new ModificadorHechos(new ArrayList<>(), new ArrayList<>());
        return modificadorHechos;

    }
}
