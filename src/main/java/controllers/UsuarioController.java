package controllers;

import models.dtos.input.ColeccionInputDTO;
import models.dtos.output.ColeccionOutputDTO;
import org.springframework.web.bind.annotation.*;
import services.IHechosService;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.IColeccionService;
import services.IHechosService;
import services.IUsuarioService;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    private IUsuarioService hechosService;

    public SolicitudHechoController(IHechosService hechosService) {
        this.hechosService = hechosService;
    }

    @GetMapping("/obtener/lista-contribuyentes")
    public ColeccionOutputDTO(@RequestBody ColeccionInputDTO dtoInput){

    }


}
