package raiz.models.entities.filtros;

import raiz.models.entities.Hecho;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class FiltroFechaAcontecimiento implements Filtro {
    private ZonedDateTime fechaInicial;
    private ZonedDateTime fechaFinal;

    public FiltroFechaAcontecimiento(ZonedDateTime fechaInicial, ZonedDateTime fechaFinal){
        this.fechaInicial = fechaInicial;
        this.fechaFinal = fechaFinal;
    }

    /// Paso entre dos fechas?
    @Override
    public Boolean aprobarHecho(Hecho hecho){

        ZonedDateTime fechaHecho = hecho.getFechaAcontecimiento();

        fechaHecho = fechaHecho.withZoneSameInstant(ZoneId.systemDefault());


        return !fechaHecho.isAfter(fechaInicial) && !fechaHecho.isBefore(fechaFinal);
    }
}
