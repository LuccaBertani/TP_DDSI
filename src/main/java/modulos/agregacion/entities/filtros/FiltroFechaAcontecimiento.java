package modulos.agregacion.entities.filtros;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "filtro_fecha_acontecimiento")
public class FiltroFechaAcontecimiento extends Filtro {

    @Column(name = "fechaInicial", length = 50)
    private ZonedDateTime fechaInicial;

    @Column(name = "fechaFinal", length = 50)
    private ZonedDateTime fechaFinal;

    public FiltroFechaAcontecimiento(ZonedDateTime fechaInicial, ZonedDateTime fechaFinal){
        this.fechaInicial = fechaInicial;
        this.fechaFinal = fechaFinal;
    }

    public FiltroFechaAcontecimiento() {

    }

    /// Paso entre dos fechas?
    @Override
    public Boolean aprobarHecho(Hecho hecho){

        ZonedDateTime fechaHecho = hecho.getAtributosHecho().getFechaAcontecimiento();

        fechaHecho = fechaHecho.withZoneSameInstant(ZoneId.systemDefault());


        return !fechaHecho.isAfter(fechaInicial) && !fechaHecho.isBefore(fechaFinal);
    }
}
