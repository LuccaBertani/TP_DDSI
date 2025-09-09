package modulos.agregacion.repositories;

import modulos.agregacion.entities.filtros.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


// No necesito consultas específicas x cada tipo de filtro -> Solo hago repo para filtro genérico
// Permite findAll y findById
public interface IFiltroRepository extends JpaRepository<Filtro, Long> {

    /* ========== FiltroCategoria ========== */
    @Query("""
           select fc
           from FiltroCategoria fc
           where fc.categoria.id = :categoriaId
           """)
    Optional<FiltroCategoria> findFiltroCategoriaByCategoriaId(@Param("categoriaId") Long categoriaId);

    /* ========== FiltroContenidoMultimedia ========== */
    @Query("""
           select fm
           from FiltroContenidoMultimedia fm
           where fm.tipoContenido = :tipo
           """)
    Optional<FiltroContenidoMultimedia> findFiltroContenidoMultimediaByTipo(@Param("tipo") String tipo);
    // Si el campo es 'tipoContenidoMultimedia' o es un enum/código Integer, cambiá la firma:
    // Optional<FiltroContenidoMultimedia> findFiltroContenidoMultimediaByTipo(@Param("tipo") Integer tipo);

    /* ========== FiltroDescripcion ========== */
    @Query("""
           select fd
           from FiltroDescripcion fd
           where fd.descripcion = :descripcion
           """)
    Optional<FiltroDescripcion> findFiltroDescripcionByDescripcion(@Param("descripcion") String descripcion);

    /* ========== FiltroTitulo ========== */
    @Query("""
           select ft
           from FiltroTitulo ft
           where ft.titulo = :titulo
           """)
    Optional<FiltroTitulo> findFiltroTituloByTitulo(@Param("titulo") String titulo);

    /* ========== FiltroFechaAcontecimiento ========== */
    @Query("""
           select fa
           from FiltroFechaAcontecimiento fa
           where fa.fechaInicial = :ini and fa.fechaFinal = :fin
           """)
    Optional<FiltroFechaAcontecimiento> findFiltroFechaAcontecimientoByRango(
            @Param("ini") LocalDate ini,
            @Param("fin") LocalDate fin
    );

    /* ========== FiltroFechaCarga ========== */
    @Query("""
           select fcg
           from FiltroFechaCarga fcg
           where fcg.fechaInicial = :ini and fcg.fechaFinal = :fin
           """)
    Optional<FiltroFechaCarga> findFiltroFechaCargaByRango(
            @Param("ini") LocalDate ini,
            @Param("fin") LocalDate fin
    );

    /* ========== FiltroOrigen ========== */
    @Query("""
           select fo
           from FiltroOrigen fo
           where fo.origenDeseado = :origen
           """)
    Optional<FiltroOrigen> findFiltroOrigenByOrigen(@Param("origen") Integer origen);
    // Si 'origenDeseado' es un enum, cambiá el tipo a tu enum.

    /* ========== FiltroPais ========== */
    @Query("""
           select fp
           from FiltroPais fp
           where fp.pais.id = :paisId
           """)
    Optional<FiltroPais> findFiltroPaisByPaisId(@Param("paisId") Long paisId);

    /* ========== FiltroProvincia ========== */
    @Query("""
           select fpr
           from FiltroProvincia fpr
           where fpr.provincia.id = :provinciaId
           """)
    Optional<FiltroProvincia> findFiltroProvinciaByProvinciaId(@Param("provinciaId") Long provinciaId);
}