package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.HechosService;
import modulos.shared.dtos.input.*;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.agregacion.entities.RespuestaHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/importar")
    public ResponseEntity<?> importarHechos(@Valid @RequestBody ImportacionHechosInputDTO dtoInput){
        return hechosService.importarHechos(dtoInput); // 201, 204 o 401
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

//hechos filtrados de all el sistema
    @GetMapping("/get")
    public ResponseEntity<?> listarHechos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false, name = "fecha_reporte_desde") String fechaReporteDesde,
            @RequestParam(required = false, name = "fecha_reporte_hasta") String fechaReporteHasta,
            @RequestParam(required = false, name = "fecha_acontecimiento_desde") String fechaAcontecimientoDesde,
            @RequestParam(required = false, name = "fecha_acontecimiento_hasta") String fechaAcontecimientoHasta,
            @RequestParam(required = false) String ubicacion
    )
    {

        GetHechosColeccionInputDTO dto = new GetHechosColeccionInputDTO();
        dto.setCategoria(categoria);
        dto.setFechaCargaInicial(fechaReporteDesde);
        dto.setFechaCargaFinal(fechaReporteHasta);
        dto.setFechaAcontecimientoInicial(fechaAcontecimientoDesde);
        dto.setFechaAcontecimientoFinal(fechaAcontecimientoHasta);
        dto.setPais(ubicacion);

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
    @PostMapping("/colecciones/refrescar")
    public ResponseEntity<?> refrescarColecciones(@Valid @RequestBody RefrescarColeccionesInputDTO inputDTO){
        return hechosService.refrescarColecciones(inputDTO.getIdUsuario());
    }

}

