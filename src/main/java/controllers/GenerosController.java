package controllers;

import models.dtos.output.GenerosOutputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.IColeccionService;
import java.util.List;

@RestController
@RequestMapping("/api/generos")
@CrossOrigin(origins = "http://localhost:3000")
public class GenerosController {
    @Autowired
    private IColeccionService generosService;

    @GetMapping
    public List<GenerosOutputDTO> listarGeneros() {
        return servicioGenero.obtenerTodos();
    }
}