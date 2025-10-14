package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.ColeccionService;
import modulos.shared.dtos.input.ColeccionInputDTO;
import modulos.shared.dtos.input.ColeccionUpdateInputDTO;
import modulos.shared.dtos.input.ModificarConsensoInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coleccion")
public class ColeccionController {

    private final ColeccionService coleccionService;

    public ColeccionController(ColeccionService coleccionService){
        this.coleccionService = coleccionService;
    }

    //anda
    @PostMapping("/crear")
    public ResponseEntity<?> crearColeccion(@Valid @RequestBody ColeccionInputDTO inputDTO){
        return coleccionService.crearColeccion(inputDTO); // 201 o 401
    }

    //anda
    @GetMapping("/get-all")
    public ResponseEntity<?> obtenerTodasLasColecciones() {
        return coleccionService.obtenerTodasLasColecciones();
    }

    //anda
    @GetMapping("/get/{id_coleccion}")
    public ResponseEntity<?> getColeccion(@PathVariable Long id_coleccion){
        return coleccionService.getColeccion(id_coleccion);
    }
    //anda
    @PostMapping("/delete")
    public ResponseEntity<?> deleteColeccion(@Valid @RequestParam Long id_coleccion, @Valid @RequestParam Long id_usuario){
        return coleccionService.deleteColeccion(id_coleccion, id_usuario);
    }
    //anda
    @PostMapping("/update")
    public ResponseEntity<?> updateColeccion(@Valid @RequestBody ColeccionUpdateInputDTO dto){
        return coleccionService.updateColeccion(dto);
    }

    @PostMapping("/add/fuente")
    public ResponseEntity<?> agregarFuente(@Valid @RequestParam Long id_usuario, @Valid @RequestParam Long id_coleccion, @Valid @RequestParam String dataSet){
        return coleccionService.agregarFuente(id_usuario, id_coleccion,dataSet);
    }

    @PostMapping("/delete/fuente")
    public ResponseEntity<?> eliminarFuente(@Valid @RequestParam Long id_usuario, @Valid @RequestParam Long id_coleccion, @Valid @RequestParam Long id_dataset){
        return coleccionService.eliminarFuente(id_usuario, id_coleccion, id_dataset);
    }

    @PostMapping("/colecciones/modificar-consenso")
    public ResponseEntity<?> modificarAlgoritmoConsenso(@RequestBody ModificarConsensoInputDTO input) {
        return coleccionService.modificarAlgoritmoConsenso(input);
    }

    @PostMapping("/colecciones/refrescar")
    public ResponseEntity<?> refrescarColecciones(@Valid @RequestParam Long id_usuario){
        return coleccionService.refrescarColecciones(id_usuario);
    }

}

