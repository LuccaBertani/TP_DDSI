package modulos.agregacion.entities.DbMain.filtros;

import modulos.agregacion.entities.DbMain.Hecho;
import org.springframework.data.jpa.domain.Specification;

public interface IFiltro {
    Boolean aprobarHecho(Hecho hecho);
    <T> Specification<T> toSpecification(Class<T> clazz);
}

