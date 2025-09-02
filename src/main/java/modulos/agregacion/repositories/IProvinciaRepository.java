package modulos.agregacion.repositories;

import modulos.agregacion.entities.Provincia;
import modulos.agregacion.entities.Ubicacion;
import modulos.agregacion.entities.projections.CategoriaProvinciaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProvinciaRepository extends JpaRepository<Provincia, Long> {
    // ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
    @Query(value = """
        SELECT p.id AS provinciaId, h.categoria_id AS categoriaId, COUNT(h.id) as cantHechos
        FROM hecho h
        JOIN ubicacion u ON h.ubicacion_id = u.id
        JOIN provincia p ON u.provincia_id = p.id
        GROUP BY p.id, h.categoria_id
        ORDER BY count(h.id) DESC
        limit 1""",nativeQuery = true)
    List<CategoriaProvinciaProjection> obtenerCategoriaMayorHechosProvincia();
}


//¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?

/*¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?*/
/*
select p.id, h.categoria_id from hecho h
join ubicacion u on h.ubicacion_id = u.id
join provincia p on u.id = p.id
group by p.id, h.categoria_id
order by count(h.id)
limit 1*/