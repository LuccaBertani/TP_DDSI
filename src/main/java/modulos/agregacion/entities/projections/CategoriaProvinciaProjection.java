package modulos.agregacion.entities.projections;

import modulos.agregacion.entities.Categoria;
import modulos.agregacion.entities.Provincia;

public interface CategoriaProvinciaProjection {
    Long getProvinciaId();
    Long getCategoriaId();
    Integer getCantHechos();
}
