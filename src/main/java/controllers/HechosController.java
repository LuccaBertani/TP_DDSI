package controllers;

import jakarta.validation.Valid;
import models.dtos.input.*;
import models.dtos.output.VisualizarHechosOutputDTO;
import models.entities.Categoria;
import models.entities.Filtrador;
import models.entities.RespuestaHttp;
import models.entities.buscadores.BuscadorCategoria;
import models.entities.filtros.Filtro;
import models.repositories.IHechosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.IHechosService;
import services.ISolicitudHechoService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/hechos")
@CrossOrigin(origins = "http://localhost:3000")
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



}

