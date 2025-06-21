package modulos.agregacion.entities;


import modulos.fuentes.Origen;
import modulos.shared.Categoria;
import modulos.shared.Hecho;
import modulos.shared.Pais;
import modulos.shared.TipoContenido;
import modulos.shared.dtos.AtributosHecho;
import modulos.shared.dtos.input.CriteriosColeccionDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorPais;
import modulos.agregacion.entities.filtros.*;
import modulos.shared.utils.FechaParser;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormateadorHecho {

public AtributosHecho formatearAtributosHecho(List<Hecho> hechosDinamica, List<Hecho> hechosEstatica, List<Hecho> hechosProxy, SolicitudHechoInputDTO dtoInput){

    AtributosHecho atributos = new AtributosHecho();

    if(dtoInput.getPais() != null) {
        Pais pais = BuscadorPais.buscarOCrear(hechosDinamica,dtoInput.getPais(),hechosProxy,hechosEstatica);
        atributos.setPais(pais);
    }else{
        Pais pais = BuscadorPais.buscarOCrear(hechosDinamica,"N/A",hechosProxy,hechosEstatica);
        atributos.setPais(pais);
    }

    atributos.setTitulo(dtoInput.getTitulo());

    if(dtoInput.getDescripcion() != null) {
        atributos.setDescripcion(dtoInput.getDescripcion());
    }else{
        atributos.setDescripcion("N/A");
    }
    if(dtoInput.getFechaAcontecimiento() != null) {
        atributos.setFechaAcontecimiento(FechaParser.parsearFecha(dtoInput.getFechaAcontecimiento()));
    }else{
        atributos.setFechaAcontecimiento(ZonedDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")));
    }
    if(dtoInput.getTipoContenido() != null){
        atributos.setContenidoMultimedia(TipoContenido.fromCodigo(dtoInput.getTipoContenido()));
    }else{
        atributos.setContenidoMultimedia(TipoContenido.INVALIDO);
    }
    if(dtoInput.getCategoria() != null){
        Categoria categoria = BuscadorCategoria.buscarOCrear(hechosDinamica,dtoInput.getCategoria(),hechosProxy,hechosEstatica);
        atributos.setCategoria(categoria);
    }
    else{
        Categoria categoria = BuscadorCategoria.buscarOCrear(hechosDinamica,"N/A",hechosProxy,hechosEstatica);
        atributos.setCategoria(categoria);
    }

    atributos.setOrigen(Origen.CARGA_MANUAL);

    return atributos;

}

public FiltrosColeccion formatearFiltrosColeccion(List<Hecho> hechosDinamica, List<Hecho> hechosEstatica, List<Hecho> hechosProxy , CriteriosColeccionDTO inputDTO){

    FiltrosColeccion filtros = new FiltrosColeccion();

    if (inputDTO.getCategoria() != null) {
        Categoria categoria = BuscadorCategoria.buscar(hechosDinamica, inputDTO.getCategoria(), hechosProxy, hechosEstatica);
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
        Pais pais = BuscadorPais.buscar(hechosDinamica, inputDTO.getPais(), hechosProxy, hechosEstatica);
        if (pais!=null)
            filtros.setFiltroPais(new FiltroPais(pais));
    }

    if (inputDTO.getTitulo() != null) {
        filtros.setFiltroTitulo(new FiltroTitulo(inputDTO.getTitulo()));
    }

    return filtros;

}

    public Map<Class<? extends Filtro>, Filtro> obtenerMapaDeFiltros(FiltrosColeccion filtrosColeccion) {
        Map<Class<? extends Filtro>, Filtro> mapa = new HashMap<>();

        if (filtrosColeccion.getFiltroCategoria() != null)
            mapa.put(FiltroCategoria.class, filtrosColeccion.getFiltroCategoria());

        if (filtrosColeccion.getFiltroPais() != null)
            mapa.put(FiltroPais.class, filtrosColeccion.getFiltroPais());

        if (filtrosColeccion.getFiltroDescripcion() != null)
            mapa.put(FiltroDescripcion.class, filtrosColeccion.getFiltroDescripcion());

        if (filtrosColeccion.getFiltroContenidoMultimedia() != null)
            mapa.put(FiltroContenidoMultimedia.class, filtrosColeccion.getFiltroContenidoMultimedia());

        if (filtrosColeccion.getFiltroFechaAcontecimiento() != null)
            mapa.put(FiltroFechaAcontecimiento.class, filtrosColeccion.getFiltroFechaAcontecimiento());

        if (filtrosColeccion.getFiltroFechaCarga() != null)
            mapa.put(FiltroFechaCarga.class, filtrosColeccion.getFiltroFechaCarga());

        if (filtrosColeccion.getFiltroOrigen() != null)
            mapa.put(FiltroOrigen.class, filtrosColeccion.getFiltroOrigen());

        if (filtrosColeccion.getFiltroTitulo() != null)
            mapa.put(FiltroTitulo.class, filtrosColeccion.getFiltroTitulo());

        return mapa;
    }

}
