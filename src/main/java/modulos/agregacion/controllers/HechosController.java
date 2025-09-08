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

    @PostMapping("/subir")
    public ResponseEntity<?> subirHecho(@Valid @RequestBody SolicitudHechoInputDTO dtoInput){
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

    // todos los hechos del sistema
    @GetMapping("/get-all")
    public ResponseEntity<?> visualizarHechos(){
        return hechosService.getAllHechos();
    }
//hechos filtrados de una coleccion
    @PostMapping("/get/filtrar")
    public ResponseEntity<?> visualizarHechosFiltrados(
            @RequestBody GetHechosColeccionInputDTO inputDTO)
    {
        return hechosService.getHechosColeccion(inputDTO);
    }

    /*
    Esta ruta expone todos los hechos del sistema y los devuelve como una lista en formato JSON. La misma acepta parámetros para filtrar los resultados:
categoría, fecha_reporte_desde, fecha_reporte_hasta,
fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion BARBARO!!.

    */

//hechos filtrados de all el sistema
    @GetMapping("/get")
    public ResponseEntity<?> listarHechos(
            @RequestParam(required = false, name = "categoria") Long categoria,
            @RequestParam(required = false, name = "fecha_reporte_desde") String fechaReporteDesde,
            @RequestParam(required = false, name = "fecha_reporte_hasta") String fechaReporteHasta,
            @RequestParam(required = false, name = "fecha_acontecimiento_desde") String fechaAcontecimientoDesde,
            @RequestParam(required = false, name = "fecha_acontecimiento_hasta") String fechaAcontecimientoHasta,
            @RequestParam(required = false, name = "pais") Long id_pais,
            @RequestParam(required = false, name = "provincia") Long id_provincia
    )
    {

        GetHechosColeccionInputDTO dto = new GetHechosColeccionInputDTO();
        dto.setCategoriaId(categoria);
        dto.setFechaCargaInicial(fechaReporteDesde);
        dto.setFechaCargaFinal(fechaReporteHasta);
        dto.setFechaAcontecimientoInicial(fechaAcontecimientoDesde);
        dto.setFechaAcontecimientoFinal(fechaAcontecimientoHasta);
        dto.setProvinciaId(id_provincia);
        dto.setPaisId(id_pais);

        return hechosService.getHechosColeccion(dto);
    }

    @GetMapping("/prueba")
    public ResponseEntity<Integer> prueba(
            @RequestParam Integer num)
    {
        return ResponseEntity.status(HttpStatus.OK.value()).body(num+1);
    }

    @PostMapping("/add/categoria")
    public ResponseEntity<?> addCategoria(@RequestParam Long id_suaurio, @RequestParam String categoriaStr, @RequestParam(required = false) List<String> sinonimos){
        return hechosService.addCategoria(id_suaurio, categoriaStr, sinonimos);
    }

    @PostMapping("/add/sinonimo/categoria")
    public ResponseEntity<?> addSinonimoCategoria(@RequestParam Long id_usuario, @RequestParam Long id_categoria, @RequestParam String sinonimo){
        return hechosService.addSinonimoCategoria(id_usuario, id_categoria, sinonimo);
    }
    @PostMapping("/add/sinonimo/pais")
    public ResponseEntity<?> addSinonimoPais(@RequestParam Long id_usuario, @RequestParam Long id_categoria, @RequestParam String sinonimo){
        return hechosService.addSinonimoPais(id_usuario, id_categoria, sinonimo);
    }
    @PostMapping("/add/sinonimo/provincia")
    public ResponseEntity<?> addSinonimoProvincia(@RequestParam Long id_usuario, @RequestParam Long id_categoria, @RequestParam String sinonimo){
        return hechosService.addSinonimoProvincia(id_usuario, id_categoria, sinonimo);
    }

}

