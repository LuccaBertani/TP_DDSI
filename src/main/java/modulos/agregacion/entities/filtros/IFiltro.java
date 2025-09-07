package modulos.agregacion.entities.filtros;

import modulos.agregacion.entities.Hecho;
import org.springframework.data.jpa.domain.Specification;

public interface IFiltro {
    Boolean aprobarHecho(Hecho hecho);
    Specification<Hecho> toSpecification();
}

