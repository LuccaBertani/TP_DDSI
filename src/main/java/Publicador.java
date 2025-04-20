import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Publicador {
    private DatosPersonalesPublicador datosPersonales; // Si se setearon, significa que el publicador inició sesión

    public void subirHechos(List<Hecho> hechos){
        hechos.forEach(hecho -> hecho.setOrigen(Origen.CONTRIBUYENTE));
        Globales.hechosTotales.addAll(hechos);
    }
}
