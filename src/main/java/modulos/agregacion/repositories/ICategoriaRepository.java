package modulos.agregacion.repositories;

import modulos.agregacion.entities.Categoria;
import modulos.agregacion.entities.CategoriaCantidad;
import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.projections.CategoriaCantidadProjection;
import modulos.agregacion.entities.projections.HoraCategoriaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query(value = """
    SELECT c.id AS categoriaId, 
        count(h.id) AS cantHechos
    FROM categoria c 
    JOIN hecho h ON h.categoria_id = c.id
    group by (c.id)
    ORDER BY COUNT(h.id) DESC 
    LIMIT 1
        """, nativeQuery = true)
    CategoriaCantidadProjection obtenerColeccionMayorHechos();


// ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?

@Query(value = """
SELECT
HOUR(h.fecha_hora) AS hora_del_dia,
COUNT(h.id)           AS totalHechos,
c.id AS Idcategoria
FROM hecho h
JOIN categoria c ON c.id = h.categoria_id
GROUP BY HOUR(h.fecha_hora), c.id
ORDER BY totalHechos DESC
LIMIT 1""",nativeQuery = true)
List<HoraCategoriaProjection> obtenerHoraMaxHechosCategoria();

}


