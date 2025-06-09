package modulos.agregacion.entities.filtros;

import modulos.shared.Hecho;
import modulos.fuentes.Origen;

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
