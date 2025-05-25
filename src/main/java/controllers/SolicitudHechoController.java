package controllers;

import jakarta.validation.Valid;
import models.dtos.input.SolicitudHechoEliminarInputDTO;
import models.dtos.input.SolicitudHechoEvaluarInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.dtos.input.SolicitudHechoModificarInputDTO;
import models.entities.RespuestaHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ISolicitudHechoService;

@RestController
@RequestMapping("/api/solicitud-hecho")
@CrossOrigin(origins = "http://localhost:3000")
public class SolicitudHechoController {

    private final ISolicitudHechoService solicitudHechoService;
    public SolicitudHechoController(ISolicitudHechoService solicitudHechoService){
        this.solicitudHechoService = solicitudHechoService;
    }

    @PostMapping("/evaluar/subir")
    public ResponseEntity<Void> evaluarSolicitudSubida(@Valid @RequestBody SolicitudHechoEvaluarInputDTO dtoInput){
        RespuestaHttp<Void> respuesta = solicitudHechoService.evaluarSolicitudSubirHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/evaluar/eliminar")
    public ResponseEntity<Void> evaluarSolicitudEliminacion(@Valid @RequestBody SolicitudHechoEvaluarInputDTO dtoInput){
        RespuestaHttp<Void> respuesta = solicitudHechoService.evaluarEliminacionHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200, 401 o 409
    }

    @PostMapping("/solicitud/subir-hecho")
    public ResponseEntity<Void> enviarSolicitudSubirHecho(@Valid @RequestBody SolicitudHechoInputDTO dtoInput){
        RespuestaHttp<Void> respuesta = solicitudHechoService.solicitarSubirHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/solicitud/eliminar-hecho")
    public ResponseEntity<Void> enviarSolicitudEliminarHecho(@Valid @RequestBody SolicitudHechoEliminarInputDTO dtoInput){
        RespuestaHttp<Void> respuesta = solicitudHechoService.solicitarEliminacionHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/solicitud/modificar-hecho")
    public ResponseEntity<Void> enviarSolicitudModificarHecho(@Valid @RequestBody SolicitudHechoModificarInputDTO dtoInput){

        if (dtoInput.getTitulo() == null || dtoInput.getTitulo().isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        RespuestaHttp<Void> respuesta = solicitudHechoService.solicitarModificacionHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

}

