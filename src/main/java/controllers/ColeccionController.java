package controllers;

import models.dtos.input.ColeccionInputDTO;
import models.dtos.input.SolicitudHechoEvaluarInputDTO;
import models.dtos.output.ColeccionOutputDTO;
import models.dtos.output.SolicitudHechoOutputDTO;
import models.entities.RespuestaHttp;
import models.repositories.IColeccionRepository;
import org.springframework.web.bind.annotation.*;
import services.IColeccionService;

@RestController
@RequestMapping("/api/coleccion")
@CrossOrigin(origins = "http://localhost:3000")
public class ColeccionController {

    private IColeccionService coleccionService;
    public ColeccionController(IColeccionService coleccionService){
        this.coleccionService = coleccionService;
    }

    @PostMapping("/crear")
    public ColeccionOutputDTO crearColeccion(@RequestBody ColeccionInputDTO dtoInput){

        //coleccionService.crearColeccion(dtoInput);
    }


}
