package raiz.controllers;

import jakarta.validation.Valid;
import raiz.models.dtos.input.FiltroHechosDTO;
import raiz.models.dtos.input.ImportacionHechosInputDTO;
import raiz.models.dtos.input.SolicitudHechoInputDTO;
import raiz.models.dtos.output.VisualizarHechosOutputDTO;
import raiz.models.entities.RespuestaHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raiz.services.IHechosService;

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
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/importar")
    public ResponseEntity<Void> importarHechos(@Valid @RequestBody ImportacionHechosInputDTO dtoInput){
        RespuestaHttp<Void> respuesta = hechosService.importarHechos(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
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

    @GetMapping("/prueba")
    public ResponseEntity<Integer> prueba(
            @RequestParam Integer num)
    {
        return ResponseEntity.status(HttpStatus.OK.value()).body(num+1);
    }

}

