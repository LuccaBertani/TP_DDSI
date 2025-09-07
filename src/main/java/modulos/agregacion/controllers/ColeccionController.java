package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.ColeccionService;
import modulos.agregacion.services.HechosService;
import modulos.shared.dtos.input.ColeccionInputDTO;
import modulos.shared.dtos.input.ColeccionUpdateInputDTO;
import modulos.shared.dtos.input.ModificarConsensoInputDTO;
import modulos.shared.dtos.input.RefrescarColeccionesInputDTO;
import modulos.shared.dtos.output.ColeccionOutputDTO;
import modulos.agregacion.entities.RespuestaHttp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coleccion")
public class ColeccionController {

    private final ColeccionService coleccionService;

    public ColeccionController(ColeccionService coleccionService, HechosService hechosService){
        this.coleccionService = coleccionService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearColeccion(@Valid @RequestBody ColeccionInputDTO inputDTO){
        return coleccionService.crearColeccion(inputDTO); // 201 o 401
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> obtenerTodasLasColecciones() {
        return coleccionService.obtenerTodasLasColecciones();
    }

    @GetMapping("/get/{id_coleccion}")
    public ResponseEntity<?> getColeccion(@PathVariable Long id_coleccion){
        return coleccionService.getColeccion(id_coleccion);
    }

    @PostMapping("/delete/{id_coleccion}")
    public ResponseEntity<?> deleteColeccion(@PathVariable Long id_coleccion){
        return coleccionService.deleteColeccion(id_coleccion);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateColeccion(@Valid @RequestBody ColeccionUpdateInputDTO dto){
        return coleccionService.updateColeccion(dto);
    }

    //Agregar o quitar fuentes de hechos de una colección.

    @PostMapping("/add/fuente")
    public ResponseEntity<?> agregarFuente(@Valid @RequestParam Long id_coleccion, @Valid @RequestParam String dataSet){
        return coleccionService.agregarFuente(id_coleccion,dataSet);
    }

    @PostMapping("/delete/fuente")
    public ResponseEntity<?> eliminarFuente(@Valid @RequestParam Long id_coleccion, @Valid @RequestParam String dataSet){
        return coleccionService.eliminarFuente(id_coleccion,dataSet);
    }

    @PostMapping("/colecciones/modificar-consenso")
    public ResponseEntity<?> modificarAlgoritmoConsenso(@RequestBody ModificarConsensoInputDTO input) {
        return coleccionService.modificarAlgoritmoConsenso(input);
    }

}

