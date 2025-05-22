package controllers;

import models.repositories.IColeccionRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.IColeccionService;
import services.IHechosService;

@RestController
@RequestMapping("/api/hechos")
@CrossOrigin(origins = "http://localhost:3000")
public class HechosController {

    private IHechosService hechosService;
    public HechosController(IHechosService hechosService){
        this.hechosService = hechosService;
    }

}
