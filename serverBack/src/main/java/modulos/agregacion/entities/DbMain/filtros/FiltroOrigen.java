package modulos.agregacion.entities.DbMain.filtros;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.DbMain.Hecho;
import modulos.agregacion.entities.atributosHecho.Origen;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

@Getter
@Setter
@Table(name = "filtro_origen")
@Entity
public class FiltroOrigen extends Filtro {
    @Enumerated(EnumType.ORDINAL)
    private Origen origenDeseado;

    public FiltroOrigen(Origen origenDeseado){
        this.origenDeseado = origenDeseado;
    }

    public FiltroOrigen() {

    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getAtributosHecho().getOrigen().equals(origenDeseado);
    }

    @Override
    public <T> Specification<T> toSpecification(Class<T> clazz) {
        return((root, query, cb) -> {
            Path<Long> pathId = root.get("atributosHecho").get("origen");
            return cb.equal(pathId,this.origenDeseado);
        });
    }

}
