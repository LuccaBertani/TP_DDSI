import java.util.ArrayList;
import java.util.List;


public class FuenteDinamica implements Fuente{
    public List<Hecho> leerFuente(){
        return Globales.hechosTotales.stream().
                filter(hecho -> hecho.getOrigen().equals(Origen.CONTRIBUYENTE))
                .toList();
    }
}
