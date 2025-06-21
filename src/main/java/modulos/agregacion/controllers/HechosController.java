package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.impl.HechosService;
import modulos.shared.dtos.input.*;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.shared.RespuestaHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/hechos")
public class HechosController {

    private final HechosService hechosService;
    public HechosController(HechosService hechosService){
        this.hechosService = hechosService;
    }

    @PostMapping("/subir")
    public ResponseEntity<Void> subirHecho(@Valid @RequestBody SolicitudHechoInputDTO dtoInput){
        RespuestaHttp<Void> respuesta = hechosService.subirHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 201 o 401
    }

    @PostMapping("/importar")
    public ResponseEntity<Void> importarHechos(@Valid @RequestBody ImportacionHechosInputDTO dtoInput){
        RespuestaHttp<Void> respuesta = hechosService.importarHechos(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 201, 204 o 401
    }

    @GetMapping("/colecciones/{identificador}/hechos")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechos( @PathVariable("identificador") Long id_coleccion){

        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.navegarPorHechos(id_coleccion);

        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());
    }

    @PostMapping("/visualizar/filtrar")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechosFiltrados(
            @RequestBody FiltroHechosDTO inputDTO)
    {

        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.navegarPorHechos(inputDTO);

        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());
    }

    @PostMapping("/ver/filtrar")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechosFiltrados(
            @RequestBody GetHechosColeccionInputDTO inputDTO)
    {

        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.getHechosColeccion(inputDTO);
        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());
    }



    @GetMapping("/visualizar/hechos")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> listarHechos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false, name = "fecha_reporte_desde") String fechaReporteDesde,
            @RequestParam(required = false, name = "fecha_reporte_hasta") String fechaReporteHasta,
            @RequestParam(required = false, name = "fecha_acontecimiento_desde") String fechaAcontecimientoDesde,
            @RequestParam(required = false, name = "fecha_acontecimiento_hasta") String fechaAcontecimientoHasta,
            @RequestParam(required = false) String ubicacion
    )
    {

        FiltroHechosDTO filtros = new FiltroHechosDTO();
        filtros.getCriterios().setCategoria(categoria);
        filtros.getCriterios().setFechaCargaInicial(fechaReporteDesde);
        filtros.getCriterios().setFechaCargaFinal(fechaReporteHasta);
        filtros.getCriterios().setFechaAcontecimientoInicial(fechaAcontecimientoDesde);
        filtros.getCriterios().setFechaAcontecimientoFinal(fechaAcontecimientoHasta);
        filtros.getCriterios().setPais(ubicacion);

        RespuestaHttp<List<VisualizarHechosOutputDTO>> respuesta = hechosService.navegarPorHechos(filtros);
        return ResponseEntity.status(respuesta.getCodigo()).body(respuesta.getDatos());
    }

    @GetMapping("/visualizar/hechos-proxy-metamapa")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechosProxyMetamapa()
    {
        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.navegarPorHechosProxyMetamapa();
        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());
    }

    @PostMapping("/colecciones/refrescar")
    public ResponseEntity<Void> refrescarColecciones(@Valid @RequestBody RefrescarColeccionesInputDTO inputDTO){
        RespuestaHttp<Void> respuesta = hechosService.refrescarColecciones(inputDTO.getIdUsuario());
        return ResponseEntity.status(respuesta.getCodigo()).build();
    }

    @GetMapping("/prueba")
    public ResponseEntity<Integer> prueba(
            @RequestParam Integer num)
    {
        return ResponseEntity.status(HttpStatus.OK.value()).body(num+1);
    }

}

