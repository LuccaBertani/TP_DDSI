package controllers;

import models.repositories.IColeccionRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.IColeccionService;

@RestController
@RequestMapping("/api/coleccion")
@CrossOrigin(origins = "http://localhost:3000")
public class ColeccionController {

    private IColeccionService coleccionService;
    public ColeccionController(IColeccionService coleccionService){
        this.coleccionService = coleccionService;
    }

}
