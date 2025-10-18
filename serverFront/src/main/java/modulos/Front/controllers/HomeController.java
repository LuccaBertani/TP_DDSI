package modulos.Front.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        // todo definir cual es la pantalla de inicio
        return "index";
    }

    @GetMapping("/404")
    public String notFound(){
        return "404";
    }

    @GetMapping("/403")
    public String accessDenied(){
        return "403";
    }
}
