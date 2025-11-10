package modulos.Front.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.input.ColeccionInputDTO;
import modulos.Front.dtos.input.SolicitudHechoEvaluarInputDTO;
import modulos.Front.dtos.input.SolicitudHechoModificarInputDTO;
import modulos.Front.dtos.output.*;
import modulos.Front.dtos.input.SolicitudHechoInputDTO;
import modulos.Front.services.ColeccionService;
import modulos.Front.services.HechosService;
import modulos.Front.services.SolicitudHechoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        Double latitud = solicitudHecho.getLatitud();
        Double longitud = solicitudHecho.getLongitud();

        if (latitud != null & longitud != null){
            ResponseEntity<?> rtaLatLon = hechosService.getPaisYProvincia(latitud, longitud);
            if (rtaLatLon.hasBody()){
                PaisProvinciaDTO paisProvinciaDTO = (PaisProvinciaDTO) rtaLatLon.getBody();
                model.addAttribute("pais", paisProvinciaDTO.getPaisDto());
                model.addAttribute("provincia", paisProvinciaDTO.getProvinciaDto());
            }
        }

        // Provincias si ya hay país seleccionado
        List<ProvinciaDto> provincias = java.util.Collections.emptyList();
        if (solicitudHecho.getId_pais() != null) {
            ResponseEntity<?> rtaProv = hechosService.getProvinciasByIdPais(solicitudHecho.getId_pais());
            if (!rtaProv.getStatusCode().is2xxSuccessful()) {
                return "redirect:/404";
            }
            provincias = BodyToListConverter.bodyToList(rtaProv, ProvinciaDto.class);
        }
        model.addAttribute("provincias", provincias);

        return "contribuir";
    }

    @PreAuthorize("hasRole('CONTRIBUYENTE')")
    @PostMapping("/solicitud-modificacion")
    public String solicitudModificacion(@Valid @ModelAttribute SolicitudHechoModificarInputDTO dto, Model model){
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

        Double latitud = dto.getLatitud();
        Double longitud = dto.getLongitud();

        if (latitud != null & longitud != null){
            ResponseEntity<?> rtaLatLon = hechosService.getPaisYProvincia(latitud, longitud);
            if (rtaLatLon.hasBody()){
                PaisProvinciaDTO paisProvinciaDTO = (PaisProvinciaDTO) rtaLatLon.getBody();
                model.addAttribute("pais", paisProvinciaDTO.getPaisDto());
                model.addAttribute("provincia", paisProvinciaDTO.getProvinciaDto());
            }
        }

        // Provincias si ya hay país seleccionado
        List<ProvinciaDto> provincias = java.util.Collections.emptyList();
        if (dto.getId_pais() != null) {
            ResponseEntity<?> rtaProv = hechosService.getProvinciasByIdPais(dto.getId_pais());
            if (!rtaProv.getStatusCode().is2xxSuccessful()) {
                return "redirect:/404";
            }
            provincias = BodyToListConverter.bodyToList(rtaProv, ProvinciaDto.class);
        }
        model.addAttribute("provincias", provincias);

        model.addAttribute("camposViejos", dto);

        return "modificar";
    }


    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/solicitudes")
    public String solicitudes(Model model, RedirectAttributes ra) {
        ResponseEntity<?> rta = solicitudHechoService.getSolicitudesPendientes();

        System.out.println(rta.getBody());

        if (rta.getStatusCode().is2xxSuccessful()) {
            List<SolicitudHechoOutputDTO> solicitudes = BodyToListConverter.bodyToList(rta, SolicitudHechoOutputDTO.class);
            model.addAttribute("solicitudes", solicitudes);
            model.addAttribute("solicitudHechoEvaluarInputDTO", new SolicitudHechoEvaluarInputDTO());
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }

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
