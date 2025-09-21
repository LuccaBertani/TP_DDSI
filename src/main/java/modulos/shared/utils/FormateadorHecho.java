package modulos.shared.utils;


import modulos.agregacion.entities.DbMain.Categoria;
import modulos.agregacion.entities.DbMain.FiltrosColeccion;
import modulos.agregacion.entities.DbMain.Pais;
import modulos.agregacion.entities.DbMain.Provincia;
import modulos.agregacion.entities.atributosHecho.AtributosHecho;
import modulos.agregacion.entities.atributosHecho.Origen;
import modulos.agregacion.entities.atributosHecho.TipoContenido;
import modulos.buscadores.*;
import modulos.shared.dtos.input.CriteriosColeccionDTO;
import modulos.shared.dtos.input.CriteriosColeccionProxyDTO;
import modulos.shared.dtos.input.ProxyDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.agregacion.entities.DbMain.filtros.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


public class FormateadorHecho {

    public static AtributosHecho formatearAtributosHecho(BuscadorUbicacion buscadorUbicacion, BuscadorCategoria buscadorCategoria, BuscadorPais buscadorPais, BuscadorProvincia buscarProvincia, SolicitudHechoInputDTO dtoInput){

    AtributosHecho atributos = new AtributosHecho();

    Pais pais = null;
    Provincia provincia = null;

    if (dtoInput.getId_pais() != null){
        pais = buscadorPais.buscar(dtoInput.getId_pais());
    }

    if (dtoInput.getId_provincia() != null){
        provincia = buscarProvincia.buscar(dtoInput.getId_provincia());
    }

    if (dtoInput.getLatitud() != null){
        atributos.setLatitud(dtoInput.getLatitud());
    }

    if (dtoInput.getLongitud() != null){
        atributos.setLongitud(dtoInput.getLongitud());
    }

    atributos.setUbicacion(buscadorUbicacion.buscarOCrear(pais,provincia));

    if (dtoInput.getId_categoria() != null){
        atributos.setCategoria(buscadorCategoria.buscar(dtoInput.getId_categoria()));
    }

    atributos.setTitulo(dtoInput.getTitulo());

    if(dtoInput.getDescripcion() != null) {
        atributos.setDescripcion(dtoInput.getDescripcion());
    }
    if(dtoInput.getFechaAcontecimiento() != null) {
        atributos.setFechaAcontecimiento(FechaParser.parsearFecha(dtoInput.getFechaAcontecimiento()));
    }else{
        atributos.setFechaAcontecimiento(null);
    }
    if(dtoInput.getTipoContenido() != null){
        atributos.setContenidoMultimedia(TipoContenido.fromCodigo(dtoInput.getTipoContenido()));
    }else{
        atributos.setContenidoMultimedia(TipoContenido.INVALIDO);
    }

    atributos.setOrigen(Origen.CARGA_MANUAL);

    return atributos;

}


    public static FiltrosColeccion formatearFiltrosColeccion(
            BuscadorFiltro buscadorFiltro,
            BuscadorCategoria buscadorCategoria,
            BuscadorPais buscadorPais,
            BuscadorProvincia buscadorProvincia,
            CriteriosColeccionProxyDTO inputDTO) {

        FiltrosColeccion filtros = new FiltrosColeccion();

        // ---------- CATEGORIA ----------
        if (inputDTO.getCategoria() != null) {
            Categoria categoria = buscadorCategoria.buscar(inputDTO.getCategoria());
            if (categoria != null) {
                var existente = buscadorFiltro.buscarFiltroCategoriaPorCategoriaId(categoria.getId());
                filtros.setFiltroCategoria(existente.orElseGet(() -> new FiltroCategoria(categoria)));
            }
        }

        // ---------- CONTENIDO MULTIMEDIA ----------
        if (inputDTO.getContenidoMultimedia() != null) {
            var existente = buscadorFiltro.buscarFiltroContenidoMultimediaPorTipo(inputDTO.getContenidoMultimedia());
            filtros.setFiltroContenidoMultimedia(existente.orElseGet(() -> new FiltroContenidoMultimedia(TipoContenido.valueOf(inputDTO.getContenidoMultimedia()))));
        }

        // ---------- DESCRIPCION ----------
        if (inputDTO.getDescripcion() != null) {
            String descripcion = inputDTO.getDescripcion();
            var existente = buscadorFiltro.buscarFiltroDescripcionExacta(descripcion);
            filtros.setFiltroDescripcion(existente.orElseGet(() -> new FiltroDescripcion(descripcion)));
        }

        // ---------- FECHA ACONTECIMIENTO ----------
        ZonedDateTime faIni = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoInicial());
        ZonedDateTime faFin = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoFinal());
        if (faIni != null && faFin != null) {
            var existente = buscadorFiltro.buscarFiltroFechaAcontecimientoPorRango(faIni.toLocalDate(), faFin.toLocalDate());
            filtros.setFiltroFechaAcontecimiento(existente.orElseGet(() -> new FiltroFechaAcontecimiento(faIni, faFin)));
        }

        // ---------- FECHA CARGA ----------
        ZonedDateTime fcIni = FechaParser.parsearFecha(inputDTO.getFechaCargaInicial());
        ZonedDateTime fcFin = FechaParser.parsearFecha(inputDTO.getFechaCargaFinal());
        if (fcIni != null && fcFin != null) {
            var existente = buscadorFiltro.buscarFiltroFechaCargaPorRango(fcIni.toLocalDate(), fcFin.toLocalDate());
            filtros.setFiltroFechaCarga(existente.orElseGet(() -> new FiltroFechaCarga(fcIni, fcFin)));
        }

        // ---------- ORIGEN ----------
        if (inputDTO.getOrigen() != null) {
            Origen origen = Origen.valueOf(inputDTO.getOrigen());
            var existente = buscadorFiltro.buscarFiltroOrigenPorValor(origen.getCodigo());
            filtros.setFiltroOrigen(existente.orElseGet(() -> new FiltroOrigen(origen)));
        }

        // ---------- PAIS ----------
        if (inputDTO.getPais() != null) {
            Pais pais = buscadorPais.buscar(inputDTO.getPais());
            if (pais != null) {
                var existente = buscadorFiltro.buscarFiltroPaisPorPaisId(pais.getId());
                filtros.setFiltroPais(existente.orElseGet(() -> new FiltroPais(pais)));
            }
        }

        // ---------- PROVINCIA ----------
        if (inputDTO.getProvincia() != null) {
            Provincia provincia = buscadorProvincia.buscar(inputDTO.getProvincia());
            if (provincia != null) {
                var existente = buscadorFiltro.buscarFiltroProvinciaPorProvinciaId(provincia.getId());
                filtros.setFiltroProvincia(existente.orElseGet(() -> new FiltroProvincia(provincia)));
            }
        }

        // ---------- TITULO ----------
        if (inputDTO.getTitulo() != null) {
            String titulo = inputDTO.getTitulo();
            var existente = buscadorFiltro.buscarFiltroTituloExacto(titulo);
            filtros.setFiltroTitulo(existente.orElseGet(() -> new FiltroTitulo(titulo)));
        }

        return filtros;
    }
    //TODO interface formateador ?)
    public static FiltrosColeccion formatearFiltrosColeccionDinamica(
            BuscadorFiltro buscadorFiltro,
            BuscadorCategoria buscadorCategoria,
            BuscadorPais buscadorPais,
            BuscadorProvincia buscadorProvincia,
            CriteriosColeccionDTO inputDTO) {

        FiltrosColeccion filtros = new FiltrosColeccion();

        // ---------- CATEGORIA (Long id) ----------
        if (inputDTO.getCategoriaId() != null) {
            Categoria categoria = buscadorCategoria.buscar(inputDTO.getCategoriaId());
            if (categoria != null) {
                var existente = buscadorFiltro.buscarFiltroCategoriaPorCategoriaId(categoria.getId());
                filtros.setFiltroCategoria(existente.orElseGet(() -> new FiltroCategoria(categoria)));
            }
        }

        // ---------- CONTENIDO MULTIMEDIA (Integer código) ----------
        if (inputDTO.getContenidoMultimedia() != null) {
            // si tu buscador acepta Integer (código):
            TipoContenido contenido = TipoContenido.fromCodigo(inputDTO.getContenidoMultimedia());

            var existente = buscadorFiltro.buscarFiltroContenidoMultimediaPorTipo(contenido.codigoEnString());

            filtros.setFiltroContenidoMultimedia(existente.orElseGet(() -> new FiltroContenidoMultimedia(contenido)));
        }

        // ---------- DESCRIPCION (String) ----------
        if (inputDTO.getDescripcion() != null) {
            String descripcion = inputDTO.getDescripcion();
            var existente = buscadorFiltro.buscarFiltroDescripcionExacta(descripcion);
            filtros.setFiltroDescripcion(existente.orElseGet(() -> new FiltroDescripcion(descripcion)));
        }

        // ---------- FECHA ACONTECIMIENTO (String -> ZonedDateTime) ----------
        ZonedDateTime faIni = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoInicial());
        ZonedDateTime faFin = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoFinal());
        if (faIni != null && faFin != null) {
            // el buscador trabaja con LocalDate para la búsqueda
            var existente = buscadorFiltro.buscarFiltroFechaAcontecimientoPorRango(
                    faIni.toLocalDate(), faFin.toLocalDate());
            filtros.setFiltroFechaAcontecimiento(existente.orElseGet(() -> new FiltroFechaAcontecimiento(faIni, faFin)));
        }

        // ---------- FECHA CARGA (String -> ZonedDateTime) ----------
        ZonedDateTime fcIni = FechaParser.parsearFecha(inputDTO.getFechaCargaInicial());
        ZonedDateTime fcFin = FechaParser.parsearFecha(inputDTO.getFechaCargaFinal());
        if (fcIni != null && fcFin != null) {
            var existente = buscadorFiltro.buscarFiltroFechaCargaPorRango(
                    fcIni.toLocalDate(), fcFin.toLocalDate());
            filtros.setFiltroFechaCarga(existente.orElseGet(() -> new FiltroFechaCarga(fcIni, fcFin)));
        }

        // ---------- ORIGEN (Integer código) ----------
        if (inputDTO.getOrigen() != null) {
            Origen origen = Origen.fromCodigo(inputDTO.getOrigen());
            // si tu buscador recibe el código (Integer):
            var existente = buscadorFiltro.buscarFiltroOrigenPorValor(origen.getCodigo());
            filtros.setFiltroOrigen(existente.orElseGet(() -> new FiltroOrigen(origen)));
        }

        // ---------- PAIS (Long id) ----------
        if (inputDTO.getPaisId() != null) {
            Pais pais = buscadorPais.buscar(inputDTO.getPaisId());
            if (pais != null) {
                var existente = buscadorFiltro.buscarFiltroPaisPorPaisId(pais.getId());
                filtros.setFiltroPais(existente.orElseGet(() -> new FiltroPais(pais)));
            }
        }

        if (inputDTO.getProvinciaId() != null) {
            Provincia provincia = buscadorProvincia.buscar(inputDTO.getProvinciaId());
            if (provincia != null) {
                System.out.println("ENTRO ACA");
                var existente = buscadorFiltro.buscarFiltroProvinciaPorProvinciaId(provincia.getId());
                filtros.setFiltroProvincia(existente.orElseGet(() -> new FiltroProvincia(provincia)));
            }
        }

        // ---------- TITULO (String) ----------
        if (inputDTO.getTitulo() != null) {
            String titulo = inputDTO.getTitulo();
            var existente = buscadorFiltro.buscarFiltroTituloExacto(titulo);
            filtros.setFiltroTitulo(existente.orElseGet(() -> new FiltroTitulo(titulo)));
        }

        return filtros;
    }


    public static ProxyDTO filtrosColeccionToString(List<Filtro> filtros) {
        ProxyDTO criterios = new ProxyDTO();

        for (Filtro filtro : filtros) {
            if (filtro instanceof FiltroCategoria) {
                Categoria categoriaObj = ((FiltroCategoria) filtro).getCategoria();
                criterios.setCategoria(categoriaObj.getTitulo());
            } else if (filtro instanceof FiltroContenidoMultimedia) {
                TipoContenido contenido = ((FiltroContenidoMultimedia) filtro).getTipoContenido();
                criterios.setContenidoMultimedia(contenido.codigoEnString());
            } else if (filtro instanceof FiltroDescripcion) {
                criterios.setDescripcion(((FiltroDescripcion) filtro).getDescripcion());
            } else if (filtro instanceof FiltroFechaAcontecimiento) {
                criterios.setFechaAcontecimientoInicial(((FiltroFechaAcontecimiento) filtro).getFechaInicial().toString());
                criterios.setFechaAcontecimientoFinal(((FiltroFechaAcontecimiento) filtro).getFechaFinal().toString());
            } else if (filtro instanceof FiltroFechaCarga) {
                criterios.setFechaCargaInicial(((FiltroFechaCarga) filtro).getFechaInicial().toString());
                criterios.setFechaCargaFinal(((FiltroFechaCarga) filtro).getFechaFinal().toString());
            } else if (filtro instanceof FiltroOrigen) {
                Origen origen = ((FiltroOrigen) filtro).getOrigenDeseado();
                criterios.setContenidoMultimedia(origen.codigoEnString());
            } else if (filtro instanceof FiltroPais) {
                Pais pais = ((FiltroPais) filtro).getPais();
                criterios.setPais(pais.getPais());
            } else if (filtro instanceof FiltroTitulo) {
                criterios.setTitulo(((FiltroTitulo) filtro).getTitulo());
            }
        }

        return criterios;
    }




    public static List<Filtro> obtenerListaDeFiltros(FiltrosColeccion filtrosColeccion) {
       List<Filtro> filtros = new ArrayList<>();

        if (filtrosColeccion.getFiltroCategoria() != null)
            filtros.add(filtrosColeccion.getFiltroCategoria());

        if (filtrosColeccion.getFiltroPais() != null)
            filtros.add(filtrosColeccion.getFiltroPais());

        if (filtrosColeccion.getFiltroDescripcion() != null)
            filtros.add(filtrosColeccion.getFiltroDescripcion());

        if (filtrosColeccion.getFiltroContenidoMultimedia() != null)
            filtros.add(filtrosColeccion.getFiltroContenidoMultimedia());

        if (filtrosColeccion.getFiltroFechaAcontecimiento() != null)
            filtros.add(filtrosColeccion.getFiltroFechaAcontecimiento());

        if (filtrosColeccion.getFiltroFechaCarga() != null)
            filtros.add(filtrosColeccion.getFiltroFechaCarga());

        if (filtrosColeccion.getFiltroOrigen() != null)
            filtros.add(filtrosColeccion.getFiltroOrigen());

        if (filtrosColeccion.getFiltroTitulo() != null)
            filtros.add(filtrosColeccion.getFiltroTitulo());

        if (filtrosColeccion.getFiltroProvincia() != null)
            filtros.add(filtrosColeccion.getFiltroProvincia());

        return filtros;
    }

}
