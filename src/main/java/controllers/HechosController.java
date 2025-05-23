package controllers;

import models.dtos.input.ImportacionHechosInputDTO;
import models.dtos.input.SolicitudHechoEliminarInputDTO;
import models.dtos.input.SolicitudHechoEvaluarInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.entities.RespuestaHttp;
import models.repositories.IHechosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.IHechosService;
import services.ISolicitudHechoService;

@RestController
@RequestMapping("/api/hecho")
@CrossOrigin(origins = "http://localhost:3000")
public class HechosController {

    private IHechosService hechosService;
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

}

