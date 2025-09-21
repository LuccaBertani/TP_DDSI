package modulos.buscadores;

import modulos.agregacion.repositories.IFiltroRepository;
import org.springframework.stereotype.Component;
import modulos.agregacion.entities.DbMain.filtros.*;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class BuscadorFiltro {

    private final IFiltroRepository filtrosRepo;

    public BuscadorFiltro(IFiltroRepository filtrosRepo) {
        this.filtrosRepo = filtrosRepo;
    }

    /* ================== Categoria ================== */
    public Optional<FiltroCategoria> buscarFiltroCategoriaPorCategoriaId(Long categoriaId) {
        if (categoriaId == null) return Optional.empty();
        return filtrosRepo.findFiltroCategoriaByCategoriaId(categoriaId);
    }

    /* ========== Contenido Multimedia (Integer/enum) ========== */
    public Optional<FiltroContenidoMultimedia> buscarFiltroContenidoMultimediaPorTipo(String tipo) {
        if (tipo == null) return Optional.empty();
        return filtrosRepo.findFiltroContenidoMultimediaByTipo(tipo);
    }

    /* ================== Descripcion (String) ================= */
    public Optional<FiltroDescripcion> buscarFiltroDescripcionExacta(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) return Optional.empty();
        return filtrosRepo.findFiltroDescripcionByDescripcion(descripcion);
    }

    /* ======= Fecha de Acontecimiento (ini / fin) ======= */
    public Optional<FiltroFechaAcontecimiento> buscarFiltroFechaAcontecimientoPorRango(LocalDate ini, LocalDate fin) {
        if (ini == null && fin == null) return Optional.empty();
        return filtrosRepo.findFiltroFechaAcontecimientoByRango(ini, fin);
    }

    /* ======= Fecha de Carga (ini / fin) ======= */
    public Optional<FiltroFechaCarga> buscarFiltroFechaCargaPorRango(LocalDate ini, LocalDate fin) {
        if (ini == null && fin == null) return Optional.empty();
        return filtrosRepo.findFiltroFechaCargaByRango(ini, fin);
    }

    /* ================== Origen (Integer/enum) ================= */
    public Optional<FiltroOrigen> buscarFiltroOrigenPorValor(Integer origen) {
        if (origen == null) return Optional.empty();
        return filtrosRepo.findFiltroOrigenByOrigen(origen);
    }

    /* ================== País (entidad) ================= */
    public Optional<FiltroPais> buscarFiltroPaisPorPaisId(Long paisId) {
        if (paisId == null) return Optional.empty();
        return filtrosRepo.findFiltroPaisByPaisId(paisId);
    }

    /* ================== Provincia (entidad) ================= */
    public Optional<FiltroProvincia> buscarFiltroProvinciaPorProvinciaId(Long provinciaId) {
        if (provinciaId == null) return Optional.empty();
        return filtrosRepo.findFiltroProvinciaByProvinciaId(provinciaId);
    }

    /* ================== Título (String) ================= */
    public Optional<FiltroTitulo> buscarFiltroTituloExacto(String titulo) {
        if (titulo == null || titulo.isBlank()) return Optional.empty();
        return filtrosRepo.findFiltroTituloByTitulo(titulo);
    }

}
