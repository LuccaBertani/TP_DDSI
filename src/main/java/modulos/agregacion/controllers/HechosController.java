package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.HechosService;
import modulos.shared.dtos.input.*;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.agregacion.entities.RespuestaHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/hechos")
public class HechosController {

    private final HechosService hechosService;
    public HechosController(HechosService hechosService){
        this.hechosService = hechosService;
    }

    //anda
    @PostMapping("/subir")
    public ResponseEntity<?> subirHecho(@Valid @RequestBody SolicitudHechoInputDTO dtoInput){
        System.out.println("id_usuario: " + dtoInput.getId_usuario());
        return hechosService.subirHecho(dtoInput); // 201 o 401
    }

    @PostMapping(
            path = "/importar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> importarHechos(
            @Valid @RequestPart("meta") ImportacionHechosInputDTO dtoInput,
            @RequestPart("file") MultipartFile file) throws IOException {
        return hechosService.importarHechos(dtoInput, file);
    }

    //anda
    // todos los hechos del sistema
    @GetMapping("/get-all")
    public ResponseEntity<?> visualizarHechos(){
        return hechosService.getAllHechos();
    }

    // Anda
    @PostMapping("/get/filtrar")
    public ResponseEntity<?> getHechosFiltradosColeccion(
            @RequestBody GetHechosColeccionInputDTO inputDTO)
    {
        return hechosService.getHechosColeccion(inputDTO);
    }


    /*
    * COLECCION 4:
    * CRITERIOS:
    * PAIS: 10
    *
    * HECHO 1
    * PAIS: 10
    * HECHO 2
    * PAIS: 10
    * HECH 3
    * PAIS: 10
    *
    * * */

    /*
    Esta ruta expone todos los hechos del sistema y los devuelve como una lista en formato JSON. La misma acepta parámetros para filtrar los resultados:
categoría, fecha_reporte_desde, fecha_reporte_hasta,
fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion BARBARO!!.

    */

//hechos filtrados de all el sistema


    // Anda
    @PostMapping("/add/categoria")
    public ResponseEntity<?> addCategoria(@RequestParam Long id_usuario, @RequestParam String categoriaStr, @RequestParam(required = false) List<String> sinonimos){
        return hechosService.addCategoria(id_usuario, categoriaStr, sinonimos);
    }

    // Anda
    @PostMapping("/add/sinonimo/categoria")
    public ResponseEntity<?> addSinonimoCategoria(@RequestParam Long id_usuario, @RequestParam Long id_categoria, @RequestParam String sinonimo){
        return hechosService.addSinonimoCategoria(id_usuario, id_categoria, sinonimo);
    }

    // Anda
    @PostMapping("/add/sinonimo/pais")
    public ResponseEntity<?> addSinonimoPais(@RequestParam Long id_usuario, @RequestParam Long id_pais, @RequestParam String sinonimo){
        return hechosService.addSinonimoPais(id_usuario, id_pais, sinonimo);
    }

    // Anda
    @PostMapping("/add/sinonimo/provincia")
    public ResponseEntity<?> addSinonimoProvincia(@RequestParam Long id_usuario, @RequestParam Long id_provincia, @RequestParam String sinonimo){
        return hechosService.addSinonimoProvincia(id_usuario, id_provincia, sinonimo);
    }

}

