package models.entities.filtros;

import models.entities.Hecho;
import models.entities.Origen;

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
