package modulos.agregacion.controllers;

import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import modulos.agregacion.services.ColeccionService;
import modulos.shared.dtos.input.ColeccionInputDTO;
import modulos.shared.dtos.input.ColeccionUpdateInputDTO;
import modulos.shared.dtos.input.ModificarConsensoInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/coleccion")
public class ColeccionController {

    private final ColeccionService coleccionService;

    public ColeccionController(ColeccionService coleccionService){
        this.coleccionService = coleccionService;
    }

    //anda
    @PostMapping("/crear")
    public ResponseEntity<?> crearColeccion(@Valid @RequestBody ColeccionInputDTO inputDTO, @AuthenticationPrincipal Jwt principal){
            return coleccionService.crearColeccion(inputDTO, principal); // 201 o 401
    }

    //anda
    @GetMapping("/public/get-all")
    public ResponseEntity<?> obtenerTodasLasColecciones() {
        return coleccionService.obtenerTodasLasColecciones();
    }

    //anda
    @GetMapping("/public/get/{id_coleccion}")
    public ResponseEntity<?> getColeccion(@PathVariable Long id_coleccion){
        return coleccionService.getColeccion(id_coleccion);
    }
    //anda
    @PostMapping("/delete")
    public ResponseEntity<?> deleteColeccion(@Valid @RequestParam Long id_coleccion, @AuthenticationPrincipal Jwt principal){
        return coleccionService.deleteColeccion(id_coleccion, principal);
    }
    //anda
    @PostMapping("/update")
    public ResponseEntity<?> updateColeccion(@Valid @RequestBody ColeccionUpdateInputDTO dto, @AuthenticationPrincipal Jwt principal){
        return coleccionService.updateColeccion(dto, principal);
    }

    @PostMapping("/add/fuente")
    public ResponseEntity<?> agregarFuente(@Valid @RequestParam Long id_coleccion, @Valid @RequestParam String dataSet, @AuthenticationPrincipal Jwt principal){
        return coleccionService.agregarFuente(id_coleccion,dataSet, principal);
    }

    @PostMapping("/delete/fuente")
    public ResponseEntity<?> eliminarFuente(@Valid @RequestParam Long id_coleccion, @Valid @RequestParam Long id_dataset, @AuthenticationPrincipal Jwt principal){
        return coleccionService.eliminarFuente(id_coleccion, id_dataset, principal);
    }

    @PostMapping("/colecciones/modificar-consenso")
    public ResponseEntity<?> modificarAlgoritmoConsenso(@RequestBody ModificarConsensoInputDTO input, @AuthenticationPrincipal Jwt principal) {
        return coleccionService.modificarAlgoritmoConsenso(input, principal);
    }

    @PostMapping("/colecciones/refrescar")
    public ResponseEntity<?> refrescarColecciones(@AuthenticationPrincipal Jwt principal){
        return coleccionService.refrescarColecciones(principal);
    }

}

