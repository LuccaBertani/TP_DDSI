package modulos.Front.controllers;

import modulos.Front.dtos.input.SolicitudHechoInputDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "index";
    }


    @GetMapping("/hechos")
    public String hechos() {
        return "hecho"; // thymeleaf busca templates/hecho.html
    }

    @GetMapping("/hecho")
    public String hecho() {
        return "hecho";
    }

    @GetMapping("/mapa")
    public String mapa() {
        return "mapa";
    }

    @GetMapping("/colecciones")
    public String colecciones() {
        return "colecciones";
    }

    @GetMapping("/contribuir")
    public String contribuir(Model model) {
        model.addAttribute("solicitudHecho", new SolicitudHechoInputDTO());
        return "contribuir";
    }

    @GetMapping("/solicitudes")
    public String solicitudes() {
        return "solicitudes";
    }

    @GetMapping("/gestion")
    public String gestion() {
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
