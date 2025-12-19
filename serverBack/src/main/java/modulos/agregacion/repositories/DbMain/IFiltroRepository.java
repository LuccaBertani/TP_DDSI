package modulos.agregacion.repositories.DbMain;

import modulos.agregacion.entities.DbMain.Fuente;
import modulos.agregacion.entities.DbMain.filtros.*;
import modulos.agregacion.entities.atributosHecho.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface IFiltroRepository extends JpaRepository<Filtro, Long> {

    @Query("""
           select fc
           from FiltroCategoria fc
           where fc.categoria.id = :categoriaId
           """)
    Optional<FiltroCategoria> findFiltroCategoriaByCategoriaId(@Param("categoriaId") Long categoriaId);

    @Query("""
           select fm
           from FiltroContenidoMultimedia fm
           where fm.tipoContenido = :tipo
           """)
    Optional<FiltroContenidoMultimedia> findFiltroContenidoMultimediaByTipo(@Param("tipo") TipoContenido tipo);

    @Query("""
           select fd
           from FiltroDescripcion fd
           where fd.descripcion = :descripcion
           """)
    Optional<FiltroDescripcion> findFiltroDescripcionByDescripcion(@Param("descripcion") String descripcion);

    @Query("""
           select ft
           from FiltroTitulo ft
           where ft.titulo = :titulo
           """)
    Optional<FiltroTitulo> findFiltroTituloByTitulo(@Param("titulo") String titulo);

    @Query("""
           select fa
           from FiltroFechaAcontecimiento fa
           where fa.fechaInicial = :ini and fa.fechaFinal = :fin
           """)
    Optional<FiltroFechaAcontecimiento> findFiltroFechaAcontecimientoByRango(
            @Param("ini") LocalDateTime ini,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
           select fcg
           from FiltroFechaCarga fcg
           where fcg.fechaInicial = :ini and fcg.fechaFinal = :fin
           """)
    Optional<FiltroFechaCarga> findFiltroFechaCargaByRango(
            @Param("ini") LocalDateTime ini,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
           select ff
           from FiltroFuente ff
           where ff.fuenteDeseada = :fuente
           """)
    Optional<FiltroFuente> findFiltroFuenteByFuente(@Param("fuente") Fuente fuente);

    @Query("""
           select fp
           from FiltroPais fp
           where fp.pais.id = :paisId
           """)
    Optional<FiltroPais> findFiltroPaisByPaisId(@Param("paisId") Long paisId);

    @Query("""
           select fpr
           from FiltroProvincia fpr
           where fpr.provincia.id = :provinciaId
           """)
    Optional<FiltroProvincia> findFiltroProvinciaByProvinciaId(@Param("provinciaId") Long provinciaId);
}