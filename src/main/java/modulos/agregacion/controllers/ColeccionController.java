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
    private final HechosService hechosService;

    public ColeccionController(ColeccionService coleccionService, HechosService hechosService){
        this.coleccionService = coleccionService;
        this.hechosService = hechosService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Void> crearColeccion(@Valid @RequestBody ColeccionInputDTO inputDTO){
        RespuestaHttp<Void> respuesta = coleccionService.crearColeccion(inputDTO);
        return ResponseEntity.status(respuesta.getCodigo()).build(); // 201 o 401
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ColeccionOutputDTO>> obtenerTodasLasColecciones() {
        RespuestaHttp<List<ColeccionOutputDTO>> respuesta = coleccionService.obtenerTodasLasColecciones();
        return ResponseEntity.status(respuesta.getCodigo()).body(respuesta.getDatos());
    }

    @GetMapping("/get")
    public ResponseEntity<ColeccionOutputDTO> getColeccion(@Valid @RequestParam Long id_coleccion){
        RespuestaHttp<ColeccionOutputDTO> respuesta = coleccionService.getColeccion(id_coleccion);
        return ResponseEntity.status(respuesta.getCodigo()).body(respuesta.getDatos());
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteColeccion(@Valid @RequestParam Long id_coleccion){
        RespuestaHttp<ColeccionOutputDTO> respuesta = coleccionService.deleteColeccion(id_coleccion);
        return ResponseEntity.status(respuesta.getCodigo()).build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateColeccion(@Valid @RequestBody ColeccionUpdateInputDTO dto){
        RespuestaHttp<Void> respuesta = coleccionService.updateColeccion(dto);
        return ResponseEntity.status(respuesta.getCodigo()).build();
    }

    //Agregar o quitar fuentes de hechos de una colección.

    @PostMapping("/add/fuente")
    public ResponseEntity<Void> agregarFuente(@Valid @RequestParam Long id_coleccion, @Valid @RequestParam String dataSet){
        RespuestaHttp<Void> respuesta = coleccionService.agregarFuente(id_coleccion,dataSet);
        return ResponseEntity.status(respuesta.getCodigo()).build();
    }

    @PostMapping("/delete/fuente")
    public ResponseEntity<Void> eliminarFuente(@Valid @RequestParam Long id_coleccion, @Valid @RequestParam String dataSet){
        RespuestaHttp<Void> respuesta = coleccionService.eliminarFuente(id_coleccion,dataSet);
        return ResponseEntity.status(respuesta.getCodigo()).build();
    }

    @PostMapping("/colecciones/modificar-consenso")
    public ResponseEntity<Void> modificarAlgoritmoConsenso(@RequestBody ModificarConsensoInputDTO input) {
        RespuestaHttp<Void> respuesta = coleccionService.modificarAlgoritmoConsenso(input);
        return ResponseEntity.status(respuesta.getCodigo()).build();
    }

    @PostMapping("/colecciones/refrescar")
    public ResponseEntity<Void> refrescarColecciones(@Valid @RequestBody RefrescarColeccionesInputDTO inputDTO){
        RespuestaHttp<Void> respuesta = hechosService.refrescarColecciones(inputDTO.getIdUsuario());
        return ResponseEntity.status(respuesta.getCodigo()).build();
    }

}

