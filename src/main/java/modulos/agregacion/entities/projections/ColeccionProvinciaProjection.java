package modulos.agregacion.entities.projections;

public interface ColeccionProvinciaProjection {
    Long getProvinciaId();
    Integer getTotalHechos();
    Long getColeccionId();
}
//De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados?
