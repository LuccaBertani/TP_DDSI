package modulos.agregacion.repositories.DbEstatica;

import modulos.agregacion.entities.DbEstatica.HechoEstatica;
import modulos.agregacion.entities.DbMain.Fuente;
import modulos.agregacion.entities.DbMain.Hecho;
import modulos.agregacion.entities.DbMain.projections.CategoriaCantidadProjection;
import modulos.agregacion.entities.DbMain.projections.CategoriaProvinciaProjection;
import modulos.agregacion.entities.DbMain.projections.HoraCategoriaProjection;
import modulos.agregacion.entities.atributosHecho.Origen;
import modulos.servicioEstadistica.entities.ProvinciaCantidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
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
SELECT COUNT(h)
FROM HechoEstatica h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    Integer findCantByNombreNormalizado(@Param("nombre") String nombre);

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
    h.fechaAcontecimiento IS NULL,
    NULL,
    HOUR(
      STR_TO_DATE(
        REPLACE(SUBSTRING(h.fechaAcontecimiento,1,19),'T',' '),
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

    @Query(value = """
        select count(d.id) from hecho_estatica h
        join hecho_dataset hd on h.id = hd.hecho_id
        join dataset d on hd.dataset_id = d.id
        where h.id = :hecho_id
""", nativeQuery = true)
    Long findCantDatasetsHecho(@Param("hecho_id") Long hecho_id);

    // h1 es el hecho distinto al que mando x parametro
    // Se verificó antes que h2 esté activo
    // Elijo no comparar al usuario
    // COALESCE es como el ISNULL de sql server
    @Query(value = """
    select count(*) 
    from hecho_estatica h1
    join hecho_estatica h2 on h2.id = :hecho_id
    where h1.activo = 1
      and h1.id <> h2.id
      and COALESCE(h1.titulo, '') = COALESCE(h2.titulo, '')
      and (
            COALESCE(h1.categoria_id, -1) <> COALESCE(h2.categoria_id, -1)
         or COALESCE(h1.tipoContenidoMultimedia, '') <> COALESCE(h2.tipoContenidoMultimedia, '')
         or COALESCE(h1.descripcion, '') <> COALESCE(h2.descripcion, '')
         or COALESCE(h1.ubicacion_id, -1) <> COALESCE(h2.ubicacion_id, -1)
         or COALESCE(h1.origen, '') <> COALESCE(h2.origen, '')
         or COALESCE(h1.fuente, '') <> COALESCE(h2.fuente, '')
         or COALESCE(h1.fechaAcontecimiento, '1900-01-01') <> COALESCE(h2.fechaAcontecimiento, '1900-01-01')
         or COALESCE(h1.fechaCarga, '1900-01-01') <> COALESCE(h2.fechaCarga, '1900-01-01')
         or COALESCE(h1.fechaUltimaActualizacion, '1900-01-01') <> COALESCE(h2.fechaUltimaActualizacion, '1900-01-01')
         or COALESCE(h1.latitud, -9999) <> COALESCE(h2.latitud, -9999)
         or COALESCE(h1.longitud, -9999) <> COALESCE(h2.longitud, -9999)
         or COALESCE(h1.modificado, 0) <> COALESCE(h2.modificado, 0)
      )
    """, nativeQuery = true)
    Long findCantHechosIgualTituloDiferentesAtributos(@Param("hecho_id") Long hechoId);

    //TODO ESTA QUERY MUGROSA NO ANDA, ME PUDRI
    @Query("""
    SELECT h
    FROM HechoEstatica h
    WHERE h.activo = true
      AND h.id <> :id
      AND (:titulo IS NULL OR h.atributosHecho.titulo = :titulo)
      AND (:categoriaId IS NULL OR h.atributosHecho.categoria_id = :categoriaId)
      AND (:descripcion IS NULL OR h.atributosHecho.descripcion = :descripcion)
      AND (:ubicacionId IS NULL OR h.atributosHecho.ubicacion_id = :ubicacionId)
      AND (:origen IS NULL OR h.atributosHecho.origen = :origen)
      AND (:fuente IS NULL OR h.atributosHecho.fuente = :fuente)
      AND (:fechaAcontecimiento IS NULL OR FUNCTION('DATE', h.atributosHecho.fechaAcontecimiento) = FUNCTION('DATE', :fechaAcontecimiento))
      AND (:latitud IS NULL OR h.atributosHecho.latitud = :latitud)
      AND (:longitud IS NULL OR h.atributosHecho.longitud = :longitud)
""")
    Optional<HechoEstatica> findHechoIdentico(
            @Param("id") Long id,
            @Param("titulo") String titulo,
            @Param("categoriaId") Long categoriaId,
            @Param("descripcion") String descripcion,
            @Param("ubicacionId") Long ubicacionId,
            @Param("origen") Origen origen,
            @Param("fuente") Fuente fuente,
            @Param("fechaAcontecimiento") LocalDateTime fechaAcontecimiento,
            @Param("latitud") Double latitud,
            @Param("longitud") Double longitud
    );

    @Query(value = """
        select h.atributosHecho.ubicacion_id from HechoEstatica h where h.id = :hecho_id
""")
    Long findUbicacionIdByHechoId(@Param("hecho_id") Long hecho_id);

    @Query(value = """
        select h from HechoEstatica h where h.atributosHecho.categoria_id = :categoria_id
""")
    List<HechoEstatica> findAllByCategoriaId(@Param("categoria_id") Long categoria_id);
    @Query(value = """
        select h from HechoEstatica h where h.activo = true
""")
    List<HechoEstatica> findAllByActivoTrue();



    @Query(value = """
        select h from HechoEstatica h where h.activo = true and h.atributosHecho.latitud is not null and h.atributosHecho.longitud is not null
""")
    List<HechoEstatica> findAllByActivoTrueAndLatitudYLongitudNotNull();


    @Query(value = """
    SELECT COUNT(h) FROM HechoEstatica h WHERE h.activo = true
    """)
    Long getCantHechos();
}