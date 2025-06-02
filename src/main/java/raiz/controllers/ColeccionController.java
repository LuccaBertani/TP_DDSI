package raiz.controllers;

import jakarta.validation.Valid;
import raiz.models.dtos.input.ColeccionInputDTO;
import raiz.models.dtos.output.ColeccionOutputDTO;
import raiz.models.entities.RespuestaHttp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raiz.services.IColeccionService;

import java.util.List;

@RestController
@RequestMapping("/api/coleccion")
public class ColeccionController {

    private final IColeccionService coleccionService;

    public ColeccionController(IColeccionService coleccionService){
        this.coleccionService = coleccionService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Void> crearColeccion(@Valid @RequestBody ColeccionInputDTO inputDTO){
        RespuestaHttp<Void> respuesta = coleccionService.crearColeccion(inputDTO);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 201 o 401
    }

    @GetMapping("/colecciones")
    public ResponseEntity<List<ColeccionOutputDTO>> obtenerTodasLasColecciones() {
        RespuestaHttp<List<ColeccionOutputDTO>> respuesta = coleccionService.obtenerTodasLasColecciones();
        return ResponseEntity.status(respuesta.getCodigo()).body(respuesta.getDatos());
    }


}

