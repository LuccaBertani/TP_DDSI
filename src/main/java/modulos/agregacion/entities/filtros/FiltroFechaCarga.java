package modulos.agregacion.entities.filtros;

import jakarta.persistence.Column;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Setter
@Getter
@Table(name = "filtro_fecha_carga")
@Entity
public class FiltroFechaCarga extends Filtro {
    @Column(name = "fecha_inicial", length = 50)
    private ZonedDateTime fechaInicial;

    @Column(name = "fecha_final", length = 50)
    private ZonedDateTime fechaFinal;

    public FiltroFechaCarga(ZonedDateTime fechaInicial, ZonedDateTime fechaFinal){
        this.fechaInicial = fechaInicial;
        this.fechaFinal = fechaFinal;
    }

    public FiltroFechaCarga() {

    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {

        ZonedDateTime fechaCarga = hecho.getAtributosHecho().getFechaCarga();

        fechaCarga = fechaCarga.withZoneSameInstant(ZoneId.systemDefault());

        return !fechaCarga.isAfter(fechaInicial) && !fechaCarga.isBefore(fechaFinal);

    }
}
