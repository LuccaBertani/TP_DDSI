package controllers;

import models.dtos.input.HechosInputDTO;
import models.dtos.output.HechosOutputDTO;
import models.repositories.IColeccionRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import services.IColeccionService;
import services.IHechosService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hechos")
@CrossOrigin(origins = "http://localhost:3000")
public class HechosController {

    private IHechosService hechosService;

    public HechosController(IHechosService hechosService){
        this.hechosService = hechosService;
    }

    @PostMapping
    public HechosOutputDTO crearHecho(@RequestBody HechosInputDTO inputDTO){
        return hechosService.crearHecho(inputDTO);
    }

    @GetMapping
    public List<HechosOutputDTO> obtenerTodosLosHechos() {
        return hechosService.obtenerTodosLosHechos();
    }

    @GetMapping("/{id}")
    public Optional<HechosOutputDTO> obtenerHechoPorId(@PathVariable Long id) {
        return hechosService.obtenerHechoPorId(id);
    }


}
