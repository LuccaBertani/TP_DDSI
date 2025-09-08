package modulos.agregacion.entities.fuentes;

import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.HechoDinamica;
import modulos.agregacion.entities.HechoEstatica;
import modulos.agregacion.entities.HechoProxy;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorHecho;
import modulos.buscadores.BuscadorPais;
import modulos.buscadores.BuscadorProvincia;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FuenteEstatica {
    private Dataset dataSet;
    public List<HechoEstatica> leerFuente(BuscadorCategoria buscadorCategoria, BuscadorPais buscadorPais, BuscadorProvincia buscadorProvincia, BuscadorHecho buscadorHecho){

        String[] nombreArchivo = this.dataSet.getStoragePath().split("\\.");
        String formato = nombreArchivo[1].toLowerCase();
        if (formato.equals("csv")){
            var lectorCSV = new LectorCSV(this.dataSet);
            return lectorCSV.leerCSV(buscadorCategoria,buscadorPais,buscadorProvincia, buscadorHecho);
        }
        else if (formato.equals("json")){
            //TODO el lector del formato JSON
            List<HechoEstatica> lista = new ArrayList<>();
            return lista;
        }

        List<HechoEstatica> lista = new ArrayList<>();
        return lista;
    }
}
