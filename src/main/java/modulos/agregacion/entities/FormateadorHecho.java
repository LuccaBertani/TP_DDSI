package modulos.agregacion.entities;


import modulos.buscadores.BuscadorProvincia;
import modulos.agregacion.entities.fuentes.Origen;
import modulos.shared.dtos.input.CriteriosColeccionDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorPais;
import modulos.agregacion.entities.filtros.*;
import modulos.shared.utils.FechaParser;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;



public class FormateadorHecho {

    public static AtributosHecho formatearAtributosHecho(Categoria categoria, Pais pais, Provincia provincia, SolicitudHechoInputDTO dtoInput){

    AtributosHecho atributos = new AtributosHecho();

    atributos.getUbicacion().setPais(pais);
    atributos.getUbicacion().setProvincia(provincia);
    atributos.setCategoria(categoria);

    atributos.setTitulo(dtoInput.getTitulo());

    if(dtoInput.getDescripcion() != null) {
        atributos.setDescripcion(dtoInput.getDescripcion());
    }else{
        atributos.setDescripcion("N/A");
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


public static FiltrosColeccion formatearFiltrosColeccion(BuscadorCategoria buscadorCategoria, BuscadorPais buscadorPais, BuscadorProvincia buscadorProvincia, CriteriosColeccionDTO inputDTO){

    FiltrosColeccion filtros = new FiltrosColeccion();

    if (inputDTO.getCategoria() != null) {
        Categoria categoria = buscadorCategoria.buscar(inputDTO.getCategoria());
        if (categoria!=null)
            filtros.setFiltroCategoria(new FiltroCategoria(categoria));
    }

    if (inputDTO.getContenidoMultimedia() != null) {
        TipoContenido contenido = TipoContenido.fromCodigo(Integer.parseInt(inputDTO.getContenidoMultimedia()));
        filtros.setFiltroContenidoMultimedia(new FiltroContenidoMultimedia(contenido));
    }

    if (inputDTO.getDescripcion() != null) {
        filtros.setFiltroDescripcion(new FiltroDescripcion(inputDTO.getDescripcion()));
    }

    ZonedDateTime fechaAcontecimientoInicial = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoInicial());
    ZonedDateTime fechaAcontecimientoFinal = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoFinal());

    if (fechaAcontecimientoInicial != null && fechaAcontecimientoFinal != null) {
        filtros.setFiltroFechaAcontecimiento(new FiltroFechaAcontecimiento(fechaAcontecimientoInicial,fechaAcontecimientoFinal));
    }

    ZonedDateTime fechaCargaInicial = FechaParser.parsearFecha(inputDTO.getFechaCargaInicial());
    ZonedDateTime fechaCargaFinal = FechaParser.parsearFecha(inputDTO.getFechaCargaFinal());

    if (inputDTO.getFechaCargaInicial() != null && inputDTO.getFechaCargaFinal() != null) {
        filtros.setFiltroFechaCarga(new FiltroFechaCarga(fechaCargaInicial,fechaCargaFinal));
    }

    if (inputDTO.getOrigen() != null) {
        Origen origen = Origen.fromCodigo(Integer.parseInt(inputDTO.getOrigen()));
        filtros.setFiltroOrigen(new FiltroOrigen(origen));
    }

    if (inputDTO.getPais() != null) {
        Pais pais = buscadorPais.buscar(inputDTO.getPais());
        if (pais!=null)
            filtros.setFiltroPais(new FiltroPais(pais));
    }

    if (inputDTO.getTitulo() != null) {
        filtros.setFiltroTitulo(new FiltroTitulo(inputDTO.getTitulo()));
    }

    return filtros;

}

    public static CriteriosColeccionDTO filtrosColeccionToString(List<Filtro> filtros) {
        CriteriosColeccionDTO criterios = new CriteriosColeccionDTO();

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

        return filtros;
    }

}
