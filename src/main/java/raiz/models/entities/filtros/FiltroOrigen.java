package raiz.models.entities.filtros;

import raiz.models.entities.Hecho;
import raiz.models.entities.Origen;

public class FiltroOrigen implements Filtro {
    private Origen origenDeseado;

    public FiltroOrigen(Origen origenDeseado){
        this.origenDeseado = origenDeseado;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getOrigen().equals(origenDeseado);
    }
}
