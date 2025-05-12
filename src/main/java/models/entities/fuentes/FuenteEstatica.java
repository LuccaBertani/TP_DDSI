package models.entities.fuentes;

import lombok.Getter;
import lombok.Setter;
import models.entities.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FuenteEstatica implements Fuente {
    @Getter
    @Setter
    private String dataSet;
    public List<Hecho> leerFuente(){

        String[] nombreArchivo = this.dataSet.split("\\.");
        String formato = nombreArchivo[1].toLowerCase();
        if (formato.equals("csv")){
            var lectorCSV = new LectorCSV(this.dataSet);
            return lectorCSV.leerCSV();
        }
        else if (formato.equals("json")){
            //TODO el lector del formato JSON
            List<Hecho>invento = new ArrayList<>();
            return invento;
        }


        List<Hecho> listaTmp = new ArrayList<>();
        return listaTmp;

    }
}
