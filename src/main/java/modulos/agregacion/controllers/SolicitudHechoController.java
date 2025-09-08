package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.SolicitudHechoService;
import modulos.shared.dtos.input.SolicitudHechoEliminarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoEvaluarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.input.SolicitudHechoModificarInputDTO;
import modulos.agregacion.entities.RespuestaHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitud-hecho")
public class SolicitudHechoController {

    private final SolicitudHechoService solicitudHechoService;

    public SolicitudHechoController(SolicitudHechoService solicitudHechoService){
        this.solicitudHechoService = solicitudHechoService;
    }

    @PostMapping("/evaluar/subir")
    public ResponseEntity<?> evaluarSolicitudSubida(@Valid @RequestBody SolicitudHechoEvaluarInputDTO dtoInput){
        return solicitudHechoService.evaluarSolicitudSubirHecho(dtoInput); // 200 o 401
    }

    @PostMapping("/evaluar/eliminar")
    public ResponseEntity<?> evaluarSolicitudEliminacion(@Valid @RequestBody SolicitudHechoEvaluarInputDTO dtoInput){
        return solicitudHechoService.evaluarEliminacionHecho(dtoInput); // 200, 401
    }

    @PostMapping("/subir-hecho")
    public ResponseEntity<?> enviarSolicitudSubirHecho(@Valid @RequestBody SolicitudHechoInputDTO dtoInput){
        return solicitudHechoService.solicitarSubirHecho(dtoInput); // 200 o 401
    }

    @PostMapping("/eliminar-hecho")
    public ResponseEntity<?> enviarSolicitudEliminarHecho(@Valid @RequestBody SolicitudHechoEliminarInputDTO dtoInput){
        return solicitudHechoService.solicitarEliminacionHecho(dtoInput); // 200 o 401
    }

    @PostMapping("/modificar-hecho")
    public ResponseEntity<?> enviarSolicitudModificarHecho(@Valid @RequestBody SolicitudHechoModificarInputDTO dtoInput){
        return solicitudHechoService.solicitarModificacionHecho(dtoInput); // 200, 401 o 409 (recurso ya modificado)
    }

    @PostMapping("/enviar-mensaje")
    public ResponseEntity<?> enviarMensajeUsuario(@Valid @RequestParam Long id_emisor, @Valid @RequestParam Long id_receptor, @Valid @RequestParam Long id_solicitud, @Valid @RequestParam String mensaje){
        return solicitudHechoService.enviarMensaje(id_emisor, id_receptor, id_solicitud, mensaje);
    }

    @GetMapping("/get-mensajes")
    public ResponseEntity<?> getMensajesUsuario(@Valid @RequestParam Long id_receptor){
        return solicitudHechoService.obtenerMensajes(id_receptor);
    }

    @PostMapping("/reportar")
    public ResponseEntity<?> reportar(@Valid @RequestParam Long id_hecho, @Valid @RequestParam String motivo){
        return solicitudHechoService.reportarHecho(motivo, id_hecho);
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllSolicitudes(@Valid @RequestParam Long id_usuario){
        return solicitudHechoService.getAllSolicitudes(id_usuario);
    }

    @GetMapping("/get/pendientes")
    public ResponseEntity<?> getSolicitudesPendientes(@Valid @RequestParam Long id_usuario){
        return solicitudHechoService.obtenerSolicitudesPendientes(id_usuario);
    }



}

