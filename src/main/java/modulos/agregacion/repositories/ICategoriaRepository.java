package modulos.agregacion.repositories;

import modulos.agregacion.entities.Categoria;
import modulos.agregacion.entities.CategoriaCantidad;
import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.Provincia;
import modulos.agregacion.entities.projections.CategoriaCantidadProjection;
import modulos.agregacion.entities.projections.HoraCategoriaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
  IF(
    h.fecha_acontecimiento IS NULL,
    NULL,
    HOUR(
      STR_TO_DATE(
        REPLACE(SUBSTRING(h.fecha_acontecimiento,1,19),'T',' '),
        '%Y-%m-%d %H:%i:%s'
      )
    )
  ) AS hora_del_dia,
  COUNT(h.id) AS totalHechos,
  c.id        AS idCategoria
FROM hecho h
JOIN categoria c ON c.id = h.categoria_id
GROUP BY hora_del_dia, c.id
ORDER BY totalHechos DESC
LIMIT 1;
""", nativeQuery = true)
    List<HoraCategoriaProjection> obtenerHoraMaxHechosCategoria();



    @Query("""
SELECT c
FROM Categoria c
WHERE
  REPLACE(LOWER(FUNCTION('unaccent', c.titulo)), ' ', '') =
  REPLACE(LOWER(FUNCTION('unaccent', :nombre)), ' ', '')
  OR EXISTS (
    SELECT 1
    FROM Sinonimo s
    WHERE s MEMBER OF c.sinonimos
      AND REPLACE(LOWER(FUNCTION('unaccent', s.sinonimoStr)), ' ', '') =
          REPLACE(LOWER(FUNCTION('unaccent', :nombre)),        ' ', '')
  )
""")
    Optional<Categoria> findByNombreNormalizado(@Param("nombre") String nombre);

}


