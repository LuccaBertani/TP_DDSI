package controllers;

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

    // TODO lo relacionado a colecciones


}
