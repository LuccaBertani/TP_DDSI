package modulos.agregacion.repositories.DbEstatica;

import modulos.agregacion.entities.DbEstatica.HechoEstatica;
import modulos.agregacion.entities.DbMain.Hecho;
import modulos.agregacion.entities.DbMain.projections.CategoriaCantidadProjection;
import modulos.agregacion.entities.DbMain.projections.CategoriaProvinciaProjection;
import modulos.agregacion.entities.DbMain.projections.HoraCategoriaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHechosEstaticaRepository extends JpaRepository<HechoEstatica, Long>, JpaSpecificationExecutor<HechoEstatica> {
    @Query("""
SELECT h
FROM HechoEstatica h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    List<HechoEstatica> findAllByNombreNormalizado(@Param("nombre") String nombre);

    @Query("""
SELECT h
FROM HechoEstatica h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    Optional<HechoEstatica> findByNombreNormalizado(@Param("nombre") String nombre);

    @Query("""
        select hecho
        from HechoEstatica hecho
        join hecho.datasets d
        where d.id = :datasetId
    """)
    List<HechoEstatica> findHechosByDataset(@Param("datasetId") Long datasetId);

    // ¿Cuál es la categoría con mayor cantidad de hechos reportados?
    @Query(value = """
        select categoria_id as categoriaId, count(h.id) as cantHechos
        from hecho_estatica h
        group by categoria_id
        order by cantHechos desc
""", nativeQuery = true)

    Optional<List<CategoriaCantidadProjection>> categoriaMayorCantHechos();



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
  ) AS horaDelDia,
  COUNT(h.id) AS totalHechos,
  h.categoria_id        AS idCategoria
FROM hecho_estatica h
GROUP BY horaDelDia, h.categoria_id
ORDER BY totalHechos DESC
LIMIT 1;
""", nativeQuery = true)
    Optional<List<HoraCategoriaProjection>> horaMayorCantHechos();



}