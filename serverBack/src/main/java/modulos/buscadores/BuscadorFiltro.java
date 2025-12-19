package modulos.buscadores;

import modulos.agregacion.entities.DbMain.Fuente;
import modulos.agregacion.entities.atributosHecho.Origen;
import modulos.agregacion.entities.atributosHecho.TipoContenido;
import modulos.agregacion.repositories.DbMain.IFiltroRepository;
import org.springframework.stereotype.Component;
import modulos.agregacion.entities.DbMain.filtros.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component
public class BuscadorFiltro {

    private final IFiltroRepository filtrosRepo;

    public BuscadorFiltro(IFiltroRepository filtrosRepo) {
        this.filtrosRepo = filtrosRepo;
    }

    public Optional<FiltroCategoria> buscarFiltroCategoriaPorCategoriaId(Long categoriaId) {
        if (categoriaId == null) return Optional.empty();
        return filtrosRepo.findFiltroCategoriaByCategoriaId(categoriaId);
    }

    public Optional<FiltroContenidoMultimedia> buscarFiltroContenidoMultimediaPorTipo(TipoContenido tipo) {
        if (tipo == null) return Optional.empty();
        return filtrosRepo.findFiltroContenidoMultimediaByTipo(TipoContenido.fromCodigo(tipo.getCodigo()));
    }

    public Optional<FiltroDescripcion> buscarFiltroDescripcionExacta(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) return Optional.empty();
        return filtrosRepo.findFiltroDescripcionByDescripcion(descripcion);
    }

    public Optional<FiltroFechaAcontecimiento> buscarFiltroFechaAcontecimientoPorRango(LocalDateTime ini, LocalDateTime fin) {
        if (ini == null && fin == null) return Optional.empty();
        return filtrosRepo.findFiltroFechaAcontecimientoByRango(ini, fin);
    }

    public Optional<FiltroFechaCarga> buscarFiltroFechaCargaPorRango(LocalDateTime ini, LocalDateTime fin) {
        if (ini == null && fin == null) return Optional.empty();
        return filtrosRepo.findFiltroFechaCargaByRango(ini, fin);
    }

    public Optional<FiltroFuente> buscarFiltroFuentePorValor(Integer fuente) {
        if (fuente == null) return Optional.empty();
        return filtrosRepo.findFiltroFuenteByFuente(Fuente.fromCodigo(fuente));
    }

    public Optional<FiltroPais> buscarFiltroPaisPorPaisId(Long paisId) {
        if (paisId == null) return Optional.empty();
        return filtrosRepo.findFiltroPaisByPaisId(paisId);
    }

    public Optional<FiltroProvincia> buscarFiltroProvinciaPorProvinciaId(Long provinciaId) {
        if (provinciaId == null) return Optional.empty();
        return filtrosRepo.findFiltroProvinciaByProvinciaId(provinciaId);
    }

    public Optional<FiltroTitulo> buscarFiltroTituloExacto(String titulo) {
        if (titulo == null || titulo.isBlank()) return Optional.empty();
        return filtrosRepo.findFiltroTituloByTitulo(titulo);
    }

}
