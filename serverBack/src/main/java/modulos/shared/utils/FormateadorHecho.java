package modulos.shared.utils;

import modulos.agregacion.entities.DbMain.*;
import modulos.agregacion.entities.atributosHecho.AtributosHecho;
import modulos.agregacion.entities.atributosHecho.Origen;
import modulos.agregacion.entities.atributosHecho.TipoContenido;
import modulos.buscadores.*;
import modulos.shared.dtos.input.CriteriosColeccionDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.agregacion.entities.DbMain.filtros.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


public class FormateadorHecho {

/* FUNCIONES ÑOQUI
    public static <T extends Hecho> T formatearHechoBDD(HechoMemoria hecho, Class<T> tipo){
        AtributosHecho atributos = AtributosHecho.builder()
                .categoria_id(hecho.getAtributosHecho().getCategoria().getId())
                .descripcion(hecho.getAtributosHecho().getDescripcion())
                .latitud(hecho.getAtributosHecho().getLatitud())
                .longitud(hecho.getAtributosHecho().getLongitud())
                .modificado(hecho.getAtributosHecho().getModificado())
                .fechaCarga(hecho.getAtributosHecho().getFechaCarga())
                .origen(hecho.getAtributosHecho().getOrigen())
                .fechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento())
                .fechaUltimaActualizacion(hecho.getAtributosHecho().getFechaUltimaActualizacion())
                .contenidosMultimedia(hecho.getAtributosHecho().getContenidoMultimedia())
                .titulo(hecho.getAtributosHecho().getTitulo())
                .ubicacion_id(hecho.getAtributosHecho().getUbicacion().getId())
                .build();

        List<AtributosHechoModificar> listaAtributosHechoModificar = new ArrayList<>();

        for (AtributosHechoModificarMemoria atributosModificar : hecho.getAtributosHechoAModificar()){
            AtributosHechoModificar atributos123 = AtributosHechoModificar.builder()
                    .id(atributosModificar.getId())
                    .latitud(atributosModificar.getLatitud())
                    .longitud(atributosModificar.getLongitud())
                    .categoria_id(atributosModificar.getCategoria().getId())
                    .titulo(atributosModificar.getTitulo())
                    .contenidoMultimedia(atributosModificar.getContenidoMultimedia())
                    .fechaAcontecimiento(atributosModificar.getFechaAcontecimiento())
                    .ubicacion_id(atributosModificar.getUbicacion().getId())
                    .build();
            listaAtributosHechoModificar.add(atributos123);
        }

        Hecho hecho123 = null;

        if (tipo == HechoDinamica.class){

            hecho123 = HechoDinamica.builder()
                        .id(hecho.getId())
                        .activo(hecho.getActivo())
                        .usuario_id(hecho.getUsuario_id())
                        .atributosHecho(atributos)
                        .atributosHechoAModificar(listaAtributosHechoModificar)
                        .build();

        }
        else if (tipo == HechoEstatica.class){
            hecho123 = HechoEstatica.builder()
                        .id(hecho.getId())
                        .activo(hecho.getActivo())
                        .usuario_id(hecho.getUsuario_id())
                        .datasets(hecho.getDatasets())
                        .atributosHecho(atributos)
                        .atributosHechoAModificar(listaAtributosHechoModificar)
                        .build();
        }
        else if (tipo == HechoProxy.class){
            hecho123 = HechoProxy.builder()
                    .id(hecho.getId())
                    .activo(hecho.getActivo())
                    .usuario_id(hecho.getUsuario_id())
                    .atributosHecho(atributos)
                    .atributosHechoAModificar(listaAtributosHechoModificar)
                    .build();
        }
        else{
            // Nunca debería entrar acá
            return null;
        }

        return tipo.cast(hecho123);
    }

    public static Hecho formatearHechoBDD(HechoMemoria hecho){
        AtributosHecho atributos = AtributosHecho.builder()
                .categoria_id(hecho.getAtributosHecho().getCategoria().getId())
                .descripcion(hecho.getAtributosHecho().getDescripcion())
                .latitud(hecho.getAtributosHecho().getLatitud())
                .longitud(hecho.getAtributosHecho().getLongitud())
                .modificado(hecho.getAtributosHecho().getModificado())
                .fechaCarga(hecho.getAtributosHecho().getFechaCarga())
                .origen(hecho.getAtributosHecho().getOrigen())
                .fechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento())
                .fechaUltimaActualizacion(hecho.getAtributosHecho().getFechaUltimaActualizacion())
                .contenidosMultimedia(hecho.getAtributosHecho().getContenidoMultimedia())
                .titulo(hecho.getAtributosHecho().getTitulo())
                .ubicacion_id(hecho.getAtributosHecho().getUbicacion().getId())
                .build();

        List<AtributosHechoModificar> listaAtributosHechoModificar = new ArrayList<>();

        for (AtributosHechoModificarMemoria atributosModificar : hecho.getAtributosHechoAModificar()){
            AtributosHechoModificar atributos123 = AtributosHechoModificar.builder()
                    .id(atributosModificar.getId())
                    .latitud(atributosModificar.getLatitud())
                    .longitud(atributosModificar.getLongitud())
                    .categoria_id(atributosModificar.getCategoria().getId())
                    .titulo(atributosModificar.getTitulo())
                    .contenidoMultimedia(atributosModificar.getContenidoMultimedia())
                    .fechaAcontecimiento(atributosModificar.getFechaAcontecimiento())
                    .ubicacion_id(atributosModificar.getUbicacion().getId())
                    .build();
            listaAtributosHechoModificar.add(atributos123);
        }

        Hecho hecho123 = null;

        switch (hecho.getAtributosHecho().getOrigen()){
            case CARGA_MANUAL, FUENTE_DINAMICA: {
                hecho123 = HechoDinamica.builder()
                        .id(hecho.getId())
                        .activo(hecho.getActivo())
                        .usuario_id(hecho.getUsuario_id())
                        .atributosHecho(atributos)
                        .atributosHechoAModificar(listaAtributosHechoModificar)
                        .build();
                break;
            }
            case FUENTE_ESTATICA: {
                hecho123 = HechoEstatica.builder()
                        .id(hecho.getId())
                        .activo(hecho.getActivo())
                        .usuario_id(hecho.getUsuario_id())
                        .datasets(hecho.getDatasets())
                        .atributosHecho(atributos)
                        .atributosHechoAModificar(listaAtributosHechoModificar)
                        .build();
                break;
            }
            case FUENTE_PROXY_METAMAPA:{
                hecho123 = HechoProxy.builder()
                        .id(hecho.getId())
                        .activo(hecho.getActivo())
                        .usuario_id(hecho.getUsuario_id())
                        .atributosHecho(atributos)
                        .atributosHechoAModificar(listaAtributosHechoModificar)
                        .build();
                break;
            }
            default:
                System.out.println("Nunca voy a entrar acá (?)");
                return null;
        }

        return hecho123;
    }

    public static HechoEstatica formatearHechoEstaticaBDD(HechoMemoria hecho){
        AtributosHecho atributos = AtributosHecho.builder()
                .categoria_id(hecho.getAtributosHecho().getCategoria().getId())
                .descripcion(hecho.getAtributosHecho().getDescripcion())
                .latitud(hecho.getAtributosHecho().getLatitud())
                .longitud(hecho.getAtributosHecho().getLongitud())
                .modificado(hecho.getAtributosHecho().getModificado())
                .fechaCarga(hecho.getAtributosHecho().getFechaCarga())
                .origen(hecho.getAtributosHecho().getOrigen())
                .fechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento())
                .fechaUltimaActualizacion(hecho.getAtributosHecho().getFechaUltimaActualizacion())
                .contenidosMultimedia(hecho.getAtributosHecho().getContenidoMultimedia())
                .titulo(hecho.getAtributosHecho().getTitulo())
                .ubicacion_id(hecho.getAtributosHecho().getUbicacion().getId())
                .build();

        List<AtributosHechoModificar> listaAtributosHechoModificar = new ArrayList<>();

        for (AtributosHechoModificarMemoria atributosModificar : hecho.getAtributosHechoAModificar()){
            AtributosHechoModificar atributos123 = AtributosHechoModificar.builder()
                    .id(atributosModificar.getId())
                    .latitud(atributosModificar.getLatitud())
                    .longitud(atributosModificar.getLongitud())
                    .categoria_id(atributosModificar.getCategoria().getId())
                    .titulo(atributosModificar.getTitulo())
                    .contenidoMultimedia(atributosModificar.getContenidoMultimedia())
                    .fechaAcontecimiento(atributosModificar.getFechaAcontecimiento())
                    .ubicacion_id(atributosModificar.getUbicacion().getId())
                    .build();
            listaAtributosHechoModificar.add(atributos123);
        }


        return HechoEstatica.builder()
                .id(hecho.getId())
                .activo(hecho.getActivo())
                .usuario_id(hecho.getUsuario_id())
                .datasets(hecho.getDatasets())
                .atributosHecho(atributos)
                .atributosHechoAModificar(listaAtributosHechoModificar)
                .build();
    }

    public static HechoDinamica formatearHechoDinamicaBDD(HechoMemoria hecho){
        AtributosHecho atributos = AtributosHecho.builder()
                .categoria_id(hecho.getAtributosHecho().getCategoria().getId())
                .descripcion(hecho.getAtributosHecho().getDescripcion())
                .latitud(hecho.getAtributosHecho().getLatitud())
                .longitud(hecho.getAtributosHecho().getLongitud())
                .modificado(hecho.getAtributosHecho().getModificado())
                .fechaCarga(hecho.getAtributosHecho().getFechaCarga())
                .origen(hecho.getAtributosHecho().getOrigen())
                .fechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento())
                .fechaUltimaActualizacion(hecho.getAtributosHecho().getFechaUltimaActualizacion())
                .contenidosMultimedia(hecho.getAtributosHecho().getContenidoMultimedia())
                .titulo(hecho.getAtributosHecho().getTitulo())
                .ubicacion_id(hecho.getAtributosHecho().getUbicacion().getId())
                .build();

        List<AtributosHechoModificar> listaAtributosHechoModificar = new ArrayList<>();

        for (AtributosHechoModificarMemoria atributosModificar : hecho.getAtributosHechoAModificar()){
            AtributosHechoModificar atributos123 = AtributosHechoModificar.builder()
                    .id(atributosModificar.getId())
                    .latitud(atributosModificar.getLatitud())
                    .longitud(atributosModificar.getLongitud())
                    .categoria_id(atributosModificar.getCategoria().getId())
                    .titulo(atributosModificar.getTitulo())
                    .contenidoMultimedia(atributosModificar.getContenidoMultimedia())
                    .fechaAcontecimiento(atributosModificar.getFechaAcontecimiento())
                    .ubicacion_id(atributosModificar.getUbicacion().getId())
                    .build();
            listaAtributosHechoModificar.add(atributos123);
        }


        return HechoDinamica.builder()
                .id(hecho.getId())
                .activo(hecho.getActivo())
                .usuario_id(hecho.getUsuario_id())
                .atributosHecho(atributos)
                .atributosHechoAModificar(listaAtributosHechoModificar)
                .build();
    }

    public static HechoProxy formatearHechoProxyBDD(HechoMemoria hecho){
        AtributosHecho atributos = AtributosHecho.builder()
                .categoria_id(hecho.getAtributosHecho().getCategoria().getId())
                .descripcion(hecho.getAtributosHecho().getDescripcion())
                .latitud(hecho.getAtributosHecho().getLatitud())
                .longitud(hecho.getAtributosHecho().getLongitud())
                .modificado(hecho.getAtributosHecho().getModificado())
                .fechaCarga(hecho.getAtributosHecho().getFechaCarga())
                .origen(hecho.getAtributosHecho().getOrigen())
                .fechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento())
                .fechaUltimaActualizacion(hecho.getAtributosHecho().getFechaUltimaActualizacion())
                .contenidosMultimedia(hecho.getAtributosHecho().getContenidoMultimedia())
                .titulo(hecho.getAtributosHecho().getTitulo())
                .ubicacion_id(hecho.getAtributosHecho().getUbicacion().getId())
                .build();

        List<AtributosHechoModificar> listaAtributosHechoModificar = new ArrayList<>();

        for (AtributosHechoModificarMemoria atributosModificar : hecho.getAtributosHechoAModificar()){
            AtributosHechoModificar atributos123 = AtributosHechoModificar.builder()
                    .id(atributosModificar.getId())
                    .latitud(atributosModificar.getLatitud())
                    .longitud(atributosModificar.getLongitud())
                    .categoria_id(atributosModificar.getCategoria().getId())
                    .titulo(atributosModificar.getTitulo())
                    .contenidoMultimedia(atributosModificar.getContenidoMultimedia())
                    .fechaAcontecimiento(atributosModificar.getFechaAcontecimiento())
                    .ubicacion_id(atributosModificar.getUbicacion().getId())
                    .build();
            listaAtributosHechoModificar.add(atributos123);
        }


        return HechoProxy.builder()
                .id(hecho.getId())
                .activo(hecho.getActivo())
                .usuario_id(hecho.getUsuario_id())
                .atributosHecho(atributos)
                .atributosHechoAModificar(listaAtributosHechoModificar)
                .build();
    }

*/

    public static AtributosHecho formatearAtributosHecho(BuscadoresRegistry buscadores, SolicitudHechoInputDTO dtoInput){

    AtributosHecho atributos = new AtributosHecho();

    Pais pais = null;
    Provincia provincia = null;

    if (dtoInput.getId_pais() != null){
        pais = buscadores.getBuscadorPais().buscar(dtoInput.getId_pais());
        if (pais != null)
            System.out.println("SOY UN PAIS FELIZ: " + pais.getPais());
    }

    if (dtoInput.getId_provincia() != null){
        provincia = buscadores.getBuscadorProvincia().buscar(dtoInput.getId_provincia());

        if (pais != null)
            System.out.println("SOY UNA PROVINCIA FELIZ: " + provincia.getProvincia());
    }

    if (dtoInput.getLatitud() != null){
        atributos.setLatitud(dtoInput.getLatitud());
    }

    if (dtoInput.getLongitud() != null){
        atributos.setLongitud(dtoInput.getLongitud());
    }

    Ubicacion ubicacion = buscadores.getBuscadorUbicacion().buscarOCrear(pais,provincia);
    atributos.setUbicacion_id(ubicacion!=null ? ubicacion.getId() : null);

    if (dtoInput.getId_categoria() != null){
        Categoria categoria = buscadores.getBuscadorCategoria().buscar(dtoInput.getId_categoria());
        atributos.setCategoria_id(categoria!=null ? categoria.getId() : null);
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

    atributos.setOrigen(Origen.CARGA_MANUAL);

    return atributos;

}


    public static FiltrosColeccion formatearFiltrosColeccion(
            BuscadoresRegistry buscadores,
            CriteriosColeccionDTO inputDTO) {

        FiltrosColeccion filtros = new FiltrosColeccion();

        // ---------- CATEGORIA ----------
        if (inputDTO.getCategoriaId() != null) {
            Categoria categoria = buscadores.getBuscadorCategoria().buscar(inputDTO.getCategoriaId());
            if (categoria != null) {
                var existente = buscadores.getBuscadorFiltro().buscarFiltroCategoriaPorCategoriaId(categoria.getId());
                filtros.setFiltroCategoria(existente.orElseGet(() -> new FiltroCategoria(categoria)));
            }
        }

        // ---------- CONTENIDO MULTIMEDIA ----------
        if (inputDTO.getContenidoMultimedia() != null) {
            var existente = buscadores.getBuscadorFiltro().buscarFiltroContenidoMultimediaPorTipo(TipoContenido.fromCodigo(inputDTO.getContenidoMultimedia()).codigoEnString());
            filtros.setFiltroContenidoMultimedia(existente.orElseGet(() -> new FiltroContenidoMultimedia(TipoContenido.fromCodigo(inputDTO.getContenidoMultimedia()))));
        }

        // ---------- DESCRIPCION ----------
        if (inputDTO.getDescripcion() != null) {
            String descripcion = inputDTO.getDescripcion();
            var existente = buscadores.getBuscadorFiltro().buscarFiltroDescripcionExacta(descripcion);
            filtros.setFiltroDescripcion(existente.orElseGet(() -> new FiltroDescripcion(descripcion)));
        }

        // ---------- FECHA ACONTECIMIENTO ----------
        ZonedDateTime faIni = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoInicial());
        ZonedDateTime faFin = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoFinal());
        if (faIni != null && faFin != null) {
            var existente = buscadores.getBuscadorFiltro().buscarFiltroFechaAcontecimientoPorRango(faIni.toLocalDate(), faFin.toLocalDate());
            filtros.setFiltroFechaAcontecimiento(existente.orElseGet(() -> new FiltroFechaAcontecimiento(faIni, faFin)));
        }

        // ---------- FECHA CARGA ----------
        ZonedDateTime fcIni = FechaParser.parsearFecha(inputDTO.getFechaCargaInicial());
        ZonedDateTime fcFin = FechaParser.parsearFecha(inputDTO.getFechaCargaFinal());
        if (fcIni != null && fcFin != null) {
            var existente = buscadores.getBuscadorFiltro().buscarFiltroFechaCargaPorRango(fcIni.toLocalDate(), fcFin.toLocalDate());
            filtros.setFiltroFechaCarga(existente.orElseGet(() -> new FiltroFechaCarga(fcIni, fcFin)));
        }

        // ---------- ORIGEN ----------
        if (inputDTO.getOrigen() != null) {
            Origen origen = Origen.fromCodigo(inputDTO.getOrigen());
            var existente = buscadores.getBuscadorFiltro().buscarFiltroOrigenPorValor(origen.getCodigo());
            filtros.setFiltroOrigen(existente.orElseGet(() -> new FiltroOrigen(origen)));
        }

        // ---------- PAIS ----------
        if (inputDTO.getPaisId() != null) {
            Pais pais = buscadores.getBuscadorPais().buscar(inputDTO.getPaisId());
            if (pais != null) {
                var existente = buscadores.getBuscadorFiltro().buscarFiltroPaisPorPaisId(pais.getId());
                filtros.setFiltroPais(existente.orElseGet(() -> new FiltroPais(pais, buscadores.getBuscadorUbicacion().buscarUbicacionesConPais(pais.getId()))));
            }
        }

        // ---------- PROVINCIA ----------
        if (inputDTO.getProvinciaId() != null) {
            Provincia provincia = buscadores.getBuscadorProvincia().buscar(inputDTO.getProvinciaId());
            if (provincia != null) {
                var existente = buscadores.getBuscadorFiltro().buscarFiltroProvinciaPorProvinciaId(provincia.getId());
                filtros.setFiltroProvincia(existente.orElseGet(() -> new FiltroProvincia(provincia,
                        buscadores.getBuscadorUbicacion().buscarUbicacionesConProvincia(provincia.getId()))));
            }
        }

        // ---------- TITULO ----------
        if (inputDTO.getTitulo() != null) {
            String titulo = inputDTO.getTitulo();
            var existente = buscadores.getBuscadorFiltro().buscarFiltroTituloExacto(titulo);
            filtros.setFiltroTitulo(existente.orElseGet(() -> new FiltroTitulo(titulo)));
        }

        return filtros;
    }
    //TODO interface formateador ?)
    public static FiltrosColeccion formatearFiltrosColeccionDinamica(
            BuscadoresRegistry buscadores,
            CriteriosColeccionDTO inputDTO) {
        BuscadorCategoria buscadorCategoria = buscadores.getBuscadorCategoria();
        BuscadorFiltro buscadorFiltro = buscadores.getBuscadorFiltro();
        BuscadorPais buscadorPais = buscadores.getBuscadorPais();
        BuscadorProvincia buscadorProvincia = buscadores.getBuscadorProvincia();

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
                filtros.setFiltroPais(existente.orElseGet(() -> new FiltroPais(pais, buscadores.getBuscadorUbicacion().buscarUbicacionesConPais(pais.getId()))));
            }
        }

        if (inputDTO.getProvinciaId() != null) {
            Provincia provincia = buscadorProvincia.buscar(inputDTO.getProvinciaId());
            if (provincia != null) {
                System.out.println("ENTRO ACA");
                var existente = buscadorFiltro.buscarFiltroProvinciaPorProvinciaId(provincia.getId());
                filtros.setFiltroProvincia(existente.orElseGet(() -> new FiltroProvincia(provincia, buscadores.getBuscadorUbicacion().buscarUbicacionesConPais(provincia.getId()))));
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


    public static CriteriosColeccionDTO filtrosColeccionToString(List<Filtro> filtros) {
        CriteriosColeccionDTO criterios = new CriteriosColeccionDTO();

        for (Filtro filtro : filtros) {
            if (filtro instanceof FiltroCategoria) {
                Categoria categoriaObj = ((FiltroCategoria) filtro).getCategoria();
                criterios.setCategoriaId(categoriaObj.getId());
            } else if (filtro instanceof FiltroContenidoMultimedia) {
                TipoContenido contenido = ((FiltroContenidoMultimedia) filtro).getTipoContenido();
                criterios.setContenidoMultimedia(contenido.getCodigo());
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
                criterios.setOrigen(origen.getCodigo());
            } else if (filtro instanceof FiltroPais) {
                Pais pais = ((FiltroPais) filtro).getPais();
                criterios.setPaisId(pais.getId());
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
