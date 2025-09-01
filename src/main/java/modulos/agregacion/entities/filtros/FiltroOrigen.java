package modulos.agregacion.entities.filtros;

import lombok.Getter;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.fuentes.Origen;
@Getter
public class FiltroOrigen implements Filtro {
    private Origen origenDeseado;

    public FiltroOrigen(Origen origenDeseado){
        this.origenDeseado = origenDeseado;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getAtributosHecho().getOrigen().equals(origenDeseado);
    }
}
