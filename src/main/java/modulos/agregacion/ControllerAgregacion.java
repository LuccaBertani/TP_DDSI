package modulos.agregacion;

import jakarta.validation.Valid;
import models.dtos.input.ColeccionInputDTO;
import models.entities.RespuestaHttp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/*
@RestController
@RequestMapping("/api/agregacion")
@CrossOrigin(origins = "http://localhost:3000")
public class ControllerAgregacion {

    private final ServiceAgregacion service;

    public ControllerAgregacion(ServiceAgregacion service) {
        this.service = service;
    }

    @PostMapping("/coleccion/crear")
    public ResponseEntity<Void> crearColeccion(@Valid @RequestBody ColeccionInputDTO inputDTO){

        RespuestaHttp<Void> respuesta = service.crearColeccion(inputDTO);
        return ResponseEntity.status(respuesta.getCodigo()).build();

    }



}
*/