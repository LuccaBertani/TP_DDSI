package raiz.models.entities.filtros;

import raiz.models.entities.Hecho;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class FiltroFechaCarga implements Filtro {
    private ZonedDateTime fechaInicial;
    private ZonedDateTime fechaFinal;

    public FiltroFechaCarga(ZonedDateTime fechaInicial, ZonedDateTime fechaFinal){
        this.fechaInicial = fechaInicial;
        this.fechaFinal = fechaFinal;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {

        ZonedDateTime fechaCarga = hecho.getFechaDeCarga();

        fechaCarga = fechaCarga.withZoneSameInstant(ZoneId.systemDefault());

        return !fechaCarga.isAfter(fechaInicial) && !fechaCarga.isBefore(fechaFinal);

    }
}
