package controllers;

import models.dtos.input.SolicitudHechoEliminarInputDTO;
import models.dtos.input.SolicitudHechoEvaluarInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.dtos.output.SolicitudHechoOutputDTO;
import models.entities.HttpCode;
import models.entities.RespuestaHttp;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IColeccionRepository;
import org.springframework.web.bind.annotation.*;
import services.IColeccionService;
import services.IHechosService;
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
    public SolicitudHechoOutputDTO evaluarSolicitudSubida(@RequestBody SolicitudHechoEvaluarInputDTO dtoInput){

        RespuestaHttp<Integer> respuesta = solicitudHechoService.evaluarSolicitudSubirHecho(dtoInput);
        SolicitudHechoOutputDTO output = new SolicitudHechoOutputDTO();
        output.setCodigoHTTP(respuesta.getCodigo());
        return output;
    }

    @PostMapping("/evaluar/eliminar")
    public SolicitudHechoOutputDTO evaluarSolicitudEliminacion(@RequestBody SolicitudHechoEvaluarInputDTO dtoInput){

        RespuestaHttp<Integer> respuesta = solicitudHechoService.evaluarEliminacionHecho(dtoInput);
        SolicitudHechoOutputDTO output = new SolicitudHechoOutputDTO();
        output.setCodigoHTTP(respuesta.getCodigo());
        return output;
    }

    @PostMapping("/solicitud/subir-hecho")
    public SolicitudHechoOutputDTO enviarSolicitudSubirHecho(@RequestBody SolicitudHechoInputDTO dtoInput){
        RespuestaHttp<Integer> respuesta = solicitudHechoService.solicitarSubirHecho(dtoInput);
        SolicitudHechoOutputDTO output = new SolicitudHechoOutputDTO();
        output.setCodigoHTTP(respuesta.getCodigo());
        return output;
    }

    @PostMapping("/solicitud/eliminar-hecho")
    public SolicitudHechoOutputDTO enviarSolicitudEliminarHecho(@RequestBody SolicitudHechoEliminarInputDTO dtoInput){
        RespuestaHttp<Integer> respuesta = solicitudHechoService.solicitarEliminacionHecho(dtoInput);
        SolicitudHechoOutputDTO output = new SolicitudHechoOutputDTO();
        output.setCodigoHTTP(respuesta.getCodigo());
        return output;
    }

}

