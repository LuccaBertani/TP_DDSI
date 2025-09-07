package modulos.agregacion.entities.filtros;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;
import org.springframework.data.jpa.domain.Specification;

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

    public Specification<Hecho> toSpecification(){
        return ((root, query, criteriaBuilder) -> {
            Path<ZonedDateTime> pathFecha = root.get("atributosHecho").get("fechaAcontecimiento");
            Predicate predicado1 = criteriaBuilder.greaterThan(pathFecha, this.fechaInicial);
            Predicate predicado2 = criteriaBuilder.lessThan(pathFecha, this.fechaFinal);
            return criteriaBuilder.and(predicado1,predicado2);
        });
    }

}
