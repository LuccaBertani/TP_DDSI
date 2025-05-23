package controllers;

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
@RequestMapping("/api/hecho")
@CrossOrigin(origins = "http://localhost:3000")
public class HechosController {

    private final IHechosService hechosService;
    public HechosController(IHechosService hechosService){
        this.hechosService = hechosService;
    }

    @PostMapping("/subir")
    public ResponseEntity<Void> subirHecho(@RequestBody SolicitudHechoInputDTO dtoInput){
        RespuestaHttp<Integer> respuesta = hechosService.subirHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/importar")
    public ResponseEntity<Void> importarHechos(@RequestBody ImportacionHechosInputDTO dtoInput){
        RespuestaHttp<Integer> respuesta = hechosService.importarHechos(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @GetMapping("/hechos")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechos(@RequestParam Long id_coleccion){

        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.navegarPorHechos(id_coleccion);

        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());

    }


    @GetMapping("/visualizar/filtrar")
    public ResponseEntity<List<VisualizarHechosOutputDTO>> visualizarHechosFiltrados(
            @RequestParam (required = false) String categoria,
            @RequestParam (required = false) String contenidoMultimedia,
            @RequestParam (required = false) String descripcion,
            @RequestParam (required = false) String fechaAcontecimientoInicial,
            @RequestParam (required = false) String fechaAcontecimientoFinal,
            @RequestParam (required = false) String fechaCargaInicial,
            @RequestParam (required = false) String fechaCargaFinal,
            @RequestParam (required = false) String origen,
            @RequestParam (required = false) String pais,
            @RequestParam (required = false) String titulo,
            @RequestParam Long id_coleccion)
    {

        List <String> filtros = new ArrayList<>();

        if(categoria != null){
            filtros.add(categoria);
        }
        else{
            filtros.add("N/A");
        }
        if(contenidoMultimedia != null){
            filtros.add(contenidoMultimedia);
        }
        else{
            filtros.add("N/A");
        }
        if(descripcion != null){
            filtros.add(descripcion);
        }
        else{
            filtros.add("N/A");
        }
        if(fechaAcontecimientoInicial != null && fechaAcontecimientoFinal != null){
            filtros.add(fechaAcontecimientoInicial);
            filtros.add(fechaAcontecimientoFinal);
        }
        else{
            filtros.add("N/A");
            filtros.add("N/A");
        }
        if(fechaCargaInicial != null && fechaCargaFinal != null){
            filtros.add(fechaCargaInicial);
            filtros.add(fechaCargaFinal);
        }
        else{
            filtros.add("N/A");
            filtros.add("N/A");
        }
        if(origen != null){
            filtros.add(origen);
        }
        else{
            filtros.add("N/A");
        }
        if(pais != null){
            filtros.add(pais);
        }
        else{
            filtros.add("N/A");
        }
        if(titulo != null){
            filtros.add(titulo);
        }
        else{
            filtros.add("N/A");
        }

        RespuestaHttp<List<VisualizarHechosOutputDTO>> outputDTO = hechosService.navegarPorHechos(filtros, id_coleccion);

        return ResponseEntity.status(outputDTO.getCodigo()).body(outputDTO.getDatos());

    }



}

