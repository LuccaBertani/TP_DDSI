package modulos.agregacion.entities.filtros;

import lombok.Data;
import modulos.agregacion.entities.Hecho;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class FiltroFechaCarga implements Filtro {
    private ZonedDateTime fechaInicial;
    private ZonedDateTime fechaFinal;

    public FiltroFechaCarga(ZonedDateTime fechaInicial, ZonedDateTime fechaFinal){
        this.fechaInicial = fechaInicial;
        this.fechaFinal = fechaFinal;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {

        ZonedDateTime fechaCarga = hecho.getAtributosHecho().getFechaCarga();

        fechaCarga = fechaCarga.withZoneSameInstant(ZoneId.systemDefault());

        return !fechaCarga.isAfter(fechaInicial) && !fechaCarga.isBefore(fechaFinal);

    }
}
