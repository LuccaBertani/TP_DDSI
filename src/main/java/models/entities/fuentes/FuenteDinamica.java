package models.entities.fuentes;

import models.entities.Globales;
import models.entities.Hecho;
import models.entities.Origen;

import java.util.List;


public class FuenteDinamica implements Fuente {
    public List<Hecho> leerFuente(){
        return Globales.hechosTotales.stream().
                filter(hecho -> hecho.getOrigen().equals(Origen.CONTRIBUYENTE))
                .toList();
    }
}
