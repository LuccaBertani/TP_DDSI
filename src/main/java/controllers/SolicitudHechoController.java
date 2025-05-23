package controllers;

import models.dtos.input.SolicitudHechoEliminarInputDTO;
import models.dtos.input.SolicitudHechoEvaluarInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.entities.RespuestaHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ISolicitudHechoService;

@RestController
@RequestMapping("/api/solicitud-hecho")
@CrossOrigin(origins = "http://localhost:3000")
public class SolicitudHechoController {

    private ISolicitudHechoService solicitudHechoService;
    public SolicitudHechoController(ISolicitudHechoService solicitudHechoService){
        this.solicitudHechoService = solicitudHechoService;
    }

    @PostMapping("/evaluar/subir")
    public ResponseEntity<Void> evaluarSolicitudSubida(@RequestBody SolicitudHechoEvaluarInputDTO dtoInput){
        RespuestaHttp<Integer> respuesta = solicitudHechoService.evaluarSolicitudSubirHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/evaluar/eliminar")
    public ResponseEntity<Void> evaluarSolicitudEliminacion(@RequestBody SolicitudHechoEvaluarInputDTO dtoInput){
        RespuestaHttp<Integer> respuesta = solicitudHechoService.evaluarEliminacionHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/solicitud/subir-hecho")
    public ResponseEntity<Void> enviarSolicitudSubirHecho(@RequestBody SolicitudHechoInputDTO dtoInput){
        RespuestaHttp<Integer> respuesta = solicitudHechoService.solicitarSubirHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/solicitud/eliminar-hecho")
    public ResponseEntity<Void> enviarSolicitudEliminarHecho(@RequestBody SolicitudHechoEliminarInputDTO dtoInput){
        RespuestaHttp<Integer> respuesta = solicitudHechoService.solicitarEliminacionHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

}

