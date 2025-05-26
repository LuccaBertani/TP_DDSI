package raiz.controllers;

import jakarta.validation.Valid;
import raiz.models.dtos.input.ColeccionInputDTO;
import raiz.models.entities.RespuestaHttp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raiz.services.IColeccionService;

@RestController
@RequestMapping("/api/coleccion")
@CrossOrigin(origins = "http://localhost:3000")
public class ColeccionController {

    private final IColeccionService coleccionService;

    public ColeccionController(IColeccionService coleccionService){
        this.coleccionService = coleccionService;
    }

    // TODO lo relacionado a colecciones
    @PostMapping("/crear")
    public ResponseEntity<Void> crearColeccion(@Valid @RequestBody ColeccionInputDTO inputDTO){

        RespuestaHttp<Void> respuesta = coleccionService.crearColeccion(inputDTO);
        return ResponseEntity.status(respuesta.getCodigo()).build();

    }

}

