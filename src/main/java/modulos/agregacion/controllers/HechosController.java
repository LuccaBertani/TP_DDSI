package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.HechosService;
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
// todos los hechos del sistema
    @GetMapping("/get-all")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechos(){
        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.getAllHechos();
        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());
    }
//hechos filtrados de una coleccion
    @PostMapping("/get/filtrar")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechosFiltrados(
            @RequestBody GetHechosColeccionInputDTO inputDTO)
    {
        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.getHechosColeccion(inputDTO);
        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());
    }

//hechos filtrados de all el sistema
    @GetMapping("/get")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> listarHechos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false, name = "fecha_reporte_desde") String fechaReporteDesde,
            @RequestParam(required = false, name = "fecha_reporte_hasta") String fechaReporteHasta,
            @RequestParam(required = false, name = "fecha_acontecimiento_desde") String fechaAcontecimientoDesde,
            @RequestParam(required = false, name = "fecha_acontecimiento_hasta") String fechaAcontecimientoHasta,
            @RequestParam(required = false) String ubicacion
    )
    {

        GetHechosColeccionInputDTO dto = new GetHechosColeccionInputDTO();
        dto.setCategoria(categoria);
        dto.setFechaCargaInicial(fechaReporteDesde);
        dto.setFechaCargaFinal(fechaReporteHasta);
        dto.setFechaAcontecimientoInicial(fechaAcontecimientoDesde);
        dto.setFechaAcontecimientoFinal(fechaAcontecimientoHasta);
        dto.setPais(ubicacion);

        RespuestaHttp<List<VisualizarHechosOutputDTO>> respuesta = hechosService.getHechosColeccion(dto);
        return ResponseEntity.status(respuesta.getCodigo()).body(respuesta.getDatos());
    }

    @GetMapping("/prueba")
    public ResponseEntity<Integer> prueba(
            @RequestParam Integer num)
    {
        return ResponseEntity.status(HttpStatus.OK.value()).body(num+1);
    }

}

