package controllers;

import models.dtos.output.SolicitudHechoOutputDTO;
import models.repositories.IColeccionRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.IColeccionService;
import services.IHechosService;
import services.ISolicitudHechoService;

@RestController
@RequestMapping("/api/solicitudHecho")
@CrossOrigin(origins = "http://localhost:3000")
public class SolicitudHechoController {

    private ISolicitudHechoService solicitudHechoService;
    public SolicitudHechoController(ISolicitudHechoService solicitudHechoService){
        this.solicitudHechoService = solicitudHechoService;
    }

    @GetMapping("/api/hechos/solicitudes/subir")
    public SolicitudHechoOutputDTO evaluarSolicitud(SolicitudHechoInputDTO dtoInput){
        return solicitudHechoService.evaluarSolicitudSubirHecho();
    }

}