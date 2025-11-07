package modulos.agregacion.controllers;

import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import modulos.JwtClaimExtractor;
import modulos.agregacion.entities.DbMain.Fuente;
import modulos.agregacion.entities.atributosHecho.OrigenConexion;
import modulos.agregacion.services.HechosService;
import modulos.shared.dtos.input.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hechos")
public class HechosController {

    private final HechosService hechosService;
    public HechosController(HechosService hechosService){
        this.hechosService = hechosService;
    }

    @GetMapping("/public/cantHechos")
    public ResponseEntity<Long> cantHechos(){
        return hechosService.getCantHechos();
    }

    //anda
    @PostMapping("/subir")
    public ResponseEntity<?> subirHecho(@Valid @RequestBody SolicitudHechoInputDTO dtoInput, @AuthenticationPrincipal String username){
        return hechosService.subirHecho(dtoInput, username); // 201 o 401
    }

    @PostMapping(
            path = "/importar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> importarHechos(
            @Valid @RequestPart("meta") ImportacionHechosInputDTO dtoInput,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal String username){
        return hechosService.importarHechos(dtoInput, file, username);
    }
    //TODO supongo que es solo fuente dinamica
    @PostMapping("/subir-archivo")
    public ResponseEntity<?> subirArchivo(@RequestParam("file") MultipartFile file, @RequestParam Long id_hecho, @AuthenticationPrincipal Jwt principal){
        return hechosService.subirArchivo(file, id_hecho, principal);
    }

    //anda
    // todos los hechos del sistema
    @GetMapping("/public/get-all")
    public ResponseEntity<?> visualizarHechos(@RequestParam Integer origen){
        System.out.println("ENTRO A GET ALL HECHOS");
        return hechosService.getAllHechos(origen);
    }

    @GetMapping("/public/get-mapa")
    public ResponseEntity<?> getHechosConLatitudYLongitud(@RequestParam Integer origen){
        System.out.println("ENTRO A getHechosConLatitudYLongitud");
        return hechosService.getHechosConLatitudYLongitud(origen);
    }

    // Anda
    @PostMapping("/public/get/filtrar")
    public ResponseEntity<?> getHechosFiltradosColeccion(
            @RequestBody GetHechosColeccionInputDTO inputDTO)
    {
        return hechosService.getHechosColeccion(inputDTO);
    }

    @GetMapping("/public/get")
    public ResponseEntity<?> getHecho(@Valid @RequestParam Long id_hecho, @Valid @RequestParam String fuente){
        return hechosService.getHecho(id_hecho, fuente);
    }

    @GetMapping("/public/paises/get-all")
    public ResponseEntity<?> getPaises(){
        return hechosService.getAllPaises();
    }

    @GetMapping("/public/provincias")
    public ResponseEntity<?> getProvinciasByIdPais(@Valid @RequestParam Long id_pais){
        return hechosService.getProvinciasByPais(id_pais);
    }
    @GetMapping("/public/categorias/get-all")
    public ResponseEntity<?> getCategorias(){
        return hechosService.getAllCategorias();
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

    // TODO OOOOOOOOOOOOOOO
    // Anda
    @PostMapping("/add/categoria")
    public ResponseEntity<?> addCategoria(@AuthenticationPrincipal Jwt principal, @RequestParam String categoriaStr, @RequestParam(required = false) List<String> sinonimos){
        return hechosService.addCategoria(principal, categoriaStr, sinonimos);
    }

    // Anda
    @PostMapping("/add/sinonimo/categoria")
    public ResponseEntity<?> addSinonimoCategoria(@AuthenticationPrincipal Jwt principal, @RequestParam Long id_categoria, @RequestParam String sinonimo){
        return hechosService.addSinonimoCategoria(principal, id_categoria, sinonimo);
    }

    // Anda
    @PostMapping("/add/sinonimo/pais")
    public ResponseEntity<?> addSinonimoPais(@AuthenticationPrincipal Jwt principal, @RequestParam Long id_pais, @RequestParam String sinonimo){
        return hechosService.addSinonimoPais(principal, id_pais, sinonimo);
    }

    // Anda
    @PostMapping("/add/sinonimo/provincia")
    public ResponseEntity<?> addSinonimoProvincia(@AuthenticationPrincipal Jwt principal, @RequestParam Long id_provincia, @RequestParam String sinonimo){
        return hechosService.addSinonimoProvincia(principal, id_provincia, sinonimo);
    }

    @GetMapping("/public/pais-provincia")
    public ResponseEntity<?> getPaisProvincia(@RequestParam Double latitud, @RequestParam Double longitud){
        return hechosService.getPaisProvincia(latitud, longitud);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mis-hechos")
    public ResponseEntity<?> getHechosDelUsuario(@AuthenticationPrincipal String username){
        System.out.println("ENTRO A GET HECHOS DEL USUARIO: " + username);
        return hechosService.getHechosDelUsuario(username);
    }

}

