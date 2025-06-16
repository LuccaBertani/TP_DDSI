package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.shared.dtos.input.SolicitudHechoEliminarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoEvaluarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.input.SolicitudHechoModificarInputDTO;
import modulos.shared.dtos.output.MensajesHechosUsuarioOutputDTO;
import modulos.shared.RespuestaHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import modulos.agregacion.services.ISolicitudHechoService;

import java.util.List;

@RestController
@RequestMapping("/api/solicitud-hecho")
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
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200, 401
    }

    @PostMapping("/subir-hecho")
    public ResponseEntity<Void> enviarSolicitudSubirHecho(@Valid @RequestBody SolicitudHechoInputDTO dtoInput){
        RespuestaHttp<Void> respuesta = solicitudHechoService.solicitarSubirHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/eliminar-hecho")
    public ResponseEntity<Void> enviarSolicitudEliminarHecho(@Valid @RequestBody SolicitudHechoEliminarInputDTO dtoInput){
        RespuestaHttp<Void> respuesta = solicitudHechoService.solicitarEliminacionHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200 o 401
    }

    @PostMapping("/modificar-hecho")
    public ResponseEntity<Void> enviarSolicitudModificarHecho(@Valid @RequestBody SolicitudHechoModificarInputDTO dtoInput){

        if (dtoInput.getTitulo() == null || dtoInput.getTitulo().isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        RespuestaHttp<Void> respuesta = solicitudHechoService.solicitarModificacionHecho(dtoInput);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 200, 401 o 409 (recurso ya modificado)
    }

    @GetMapping("/mensajes")
    public ResponseEntity<List<MensajesHechosUsuarioOutputDTO>> enviarMensajesUsuario(@RequestParam Long id_usuario){
        RespuestaHttp<List<MensajesHechosUsuarioOutputDTO>> respuesta = solicitudHechoService.enviarMensajes(id_usuario);
        return ResponseEntity.status(respuesta.getCodigo()).body(respuesta.getDatos());
    }

    @PostMapping("/reportar")
    public ResponseEntity<Void> reportar(@Valid @RequestParam Long id_hecho, @Valid @RequestParam String motivo){
        RespuestaHttp<Void> respuesta = solicitudHechoService.reportarHecho(motivo, id_hecho);
        return ResponseEntity.status(respuesta.getCodigo()).body(respuesta.getDatos());
    }

}

