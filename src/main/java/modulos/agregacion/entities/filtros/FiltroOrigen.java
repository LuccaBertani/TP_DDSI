package modulos.agregacion.entities.filtros;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.Path;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.fuentes.Origen;
import org.springframework.data.jpa.domain.Specification;

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
    public Specification<Hecho> toSpecification() {
        return((root, query, cb) -> {
            Path<Long> pathId = root.get("atributosHecho").get("contenidoMultimedia");
            return cb.equal(pathId,this.origenDeseado);
        });
    }
}
