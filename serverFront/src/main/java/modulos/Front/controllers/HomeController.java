package modulos.Front.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.input.ColeccionInputDTO;
import modulos.Front.dtos.output.CategoriaDto;
import modulos.Front.dtos.input.SolicitudHechoInputDTO;
import modulos.Front.dtos.output.PaisDto;
import modulos.Front.dtos.output.ProvinciaDto;
import modulos.Front.services.ColeccionService;
import modulos.Front.services.HechosService;
import modulos.Front.services.SolicitudHechoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class HomeController {

    private final HechosService hechosService;
    private final ColeccionService coleccionService;
    private final SolicitudHechoService solicitudHechoService;

    @GetMapping("/")
    public String home(Model model) {
        ResponseEntity<Long> statsHechos = hechosService.getCantHechos();
        ResponseEntity<Long> statsColecciones = coleccionService.getCantColecciones();
        ResponseEntity<Integer> statsSolicitudes = solicitudHechoService.getPorcentajeSolicitudesProcesadas();

        model.addAttribute("statsHechos", statsHechos.getBody());
        model.addAttribute("statsColecciones", statsColecciones.getBody());
        model.addAttribute("statsSolicitudes", statsSolicitudes.getBody());

        return "index";
    }

    @GetMapping("/public/mapa")
    public String mapa() {
        return "mapa";
    }

    /*@GetMapping("/contribuir")
    public String contribuir(@RequestParam(required = false) Long pais_id,
                             @ModelAttribute SolicitudHechoInputDTO solicitudHecho,
                             Model model) {

        ResponseEntity <?> rta = this.hechosService.getPaises();
        ResponseEntity <?> rta2 = this.hechosService.getCategorias();

        if(!rta.getStatusCode().is2xxSuccessful() || !rta2.getStatusCode().is2xxSuccessful()){
            return "redirect:/404";
        }

        List<PaisDto> paises = BodyToListConverter.bodyToList(rta, PaisDto.class);
        List<CategoriaDto> categorias = BodyToListConverter.bodyToList(rta2, CategoriaDto.class);

        model.addAttribute("paises", paises);
        model.addAttribute("categorias", categorias);


        if (solicitudHecho == null){
            System.out.println("soli hecho de mierda es null");
            model.addAttribute("solicitudHecho", new SolicitudHechoInputDTO());
            return "contribuir";
        }
        else{
            System.out.println("soli hecho de mierda NOOO es null");
            model.addAttribute("solicitudHecho", solicitudHecho);
            ResponseEntity <?> rta3 = this.hechosService.getProvinciasByIdPais(pais_id);
            if(!rta3.getStatusCode().is2xxSuccessful()){
                return "redirect:/404";
            }
            List<ProvinciaDto> provincias = BodyToListConverter.bodyToList(rta3, ProvinciaDto.class);
            model.addAttribute("provincias", provincias);
            return "contribuir";
        }

    }*/


    @GetMapping("/public/contribuir")
    public String contribuir(
            @ModelAttribute("solicitudHecho") SolicitudHechoInputDTO solicitudHecho,
            Model model, HttpSession httpSession) {

        // Catálogos base
        ResponseEntity<?> rtaPaises = hechosService.getPaises();
        ResponseEntity<?> rtaCategorias = hechosService.getCategorias();
        if (!rtaPaises.getStatusCode().is2xxSuccessful() || !rtaCategorias.getStatusCode().is2xxSuccessful()) {
            return "redirect:/404";
        }

        List<PaisDto> paises = BodyToListConverter.bodyToList(rtaPaises, PaisDto.class);
        List<CategoriaDto> categorias = BodyToListConverter.bodyToList(rtaCategorias, CategoriaDto.class);
        model.addAttribute("paises", paises);
        model.addAttribute("categorias", categorias);

        // Provincias si ya hay país seleccionado
        List<ProvinciaDto> provincias = java.util.Collections.emptyList();
        if (solicitudHecho != null && solicitudHecho.getId_pais() != null) {
            ResponseEntity<?> rtaProv = hechosService.getProvinciasByIdPais(solicitudHecho.getId_pais());
            if (!rtaProv.getStatusCode().is2xxSuccessful()) {
                return "redirect:/404";
            }
            provincias = BodyToListConverter.bodyToList(rtaProv, ProvinciaDto.class);
        }
        model.addAttribute("provincias", provincias);

        return "contribuir";
    }


    /*@GetMapping("/contribuir")
    public String contribuir(
            @RequestParam(required = false) Long pais_id,
            @RequestParam(required = false) Long provincia_id,
            @ModelAttribute SolicitudHechoInputDTO solicitudHecho,
            Model model) {

        model.addAttribute("solicitudHecho", solicitudHecho); // mantiene datos previos

        // Obtener países y categorías
        ResponseEntity<?> rtaPaises = hechosService.getPaises();
        ResponseEntity<?> rtaCategorias = hechosService.getCategorias();

        if (!rtaPaises.getStatusCode().is2xxSuccessful() || !rtaCategorias.getStatusCode().is2xxSuccessful()) {
            return "redirect:/404";
        }

        List<PaisDto> paises = BodyToListConverter.bodyToList(rtaPaises, PaisDto.class);
        List<CategoriaDto> categorias = BodyToListConverter.bodyToList(rtaCategorias, CategoriaDto.class);
        model.addAttribute("paises", paises);
        model.addAttribute("categorias", categorias);

        // Si el usuario ya seleccionó un país, cargar provincias relacionadas
        List<ProvinciaDto> provincias = List.of();
        if (pais_id != null) {
            ResponseEntity<?> rtaProvincias = hechosService.getProvinciasByIdPais(pais_id);
            if (rtaProvincias.getStatusCode().is2xxSuccessful()) {
                provincias = BodyToListConverter.bodyToList(rtaProvincias, ProvinciaDto.class);

                // También podrías guardar el país seleccionado completo si lo necesitás
                paises.stream()
                        .filter(p -> p.getId().equals(pais_id))
                        .findFirst()
                        .ifPresent(p -> model.addAttribute("pais", p));
            }
        }

        model.addAttribute("provincias", provincias);
        model.addAttribute("paisSeleccionado", pais_id);
        model.addAttribute("provinciaSeleccionada", provincia_id);

        return "contribuir";
    }
*/


    @GetMapping("/solicitudes")
    public String solicitudes() {
        return "solicitudes";
    }

    @GetMapping("/gestion")
    public String gestion(Model model) {
        model.addAttribute("coleccionForm", new ColeccionInputDTO());
        return "gestion";
    }



    @GetMapping("/404")
    public String notFound(){
        return "404";
    }

    @GetMapping("/500")
    public String internalServerError(){
        return "500";
    }

    @GetMapping("/403")
    public String accessDenied(){
        return "403";
    }
}
