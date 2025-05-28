package raiz.controllers;

import jakarta.validation.Valid;
import raiz.models.dtos.input.FiltroHechosDTO;
import raiz.models.dtos.input.ImportacionHechosInputDTO;
import raiz.models.dtos.input.SolicitudHechoInputDTO;
import raiz.models.dtos.output.VisualizarHechosOutputDTO;
import raiz.models.entities.Categoria;
import raiz.models.entities.FechaParser;
import raiz.models.entities.Hecho;
import raiz.models.entities.RespuestaHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raiz.models.entities.buscadores.BuscadorCategoria;
import raiz.models.entities.buscadores.BuscadorPais;
import raiz.models.entities.filtros.*;
import raiz.services.IHechosService;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/hechos")
public class HechosController {

    private final IHechosService hechosService;
    public HechosController(IHechosService hechosService){
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
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 201 o 401
    }

    @GetMapping("/colecciones/{identificador}/hechos")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechos( @PathVariable("identificador") Long id_coleccion){

        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.navegarPorHechos(id_coleccion);

        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());

    }

    @GetMapping("/visualizar/filtrar")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechosFiltrados(
            @RequestBody FiltroHechosDTO inputDTO)
    {

        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.navegarPorHechos(inputDTO);

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
    ) {

        FiltroHechosDTO filtros = new FiltroHechosDTO();
        filtros.setCategoria(categoria);
        filtros.setFechaCargaInicial(fechaReporteDesde);
        filtros.setFechaCargaFinal(fechaReporteHasta);
        filtros.setFechaAcontecimientoInicial(fechaAcontecimientoDesde);
        filtros.setFechaAcontecimientoFinal(fechaAcontecimientoHasta);
        filtros.setPais(ubicacion);

        RespuestaHttp<List<VisualizarHechosOutputDTO>> respuesta = hechosService.navegarPorHechos(filtros);
        return ResponseEntity.status(respuesta.getCodigo()).body(respuesta.getDatos());
    }

    @GetMapping("visualizar/hechos-proxy-metamapa")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechosProxyMetamapa()
    {
        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.navegarPorHechosProxyMetamapa();
        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());
    }

    @GetMapping("/prueba")
    public ResponseEntity<Integer> prueba(
            @RequestParam Integer num)
    {
        return ResponseEntity.status(HttpStatus.OK.value()).body(num+1);
    }

}

