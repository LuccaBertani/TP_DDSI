package modulos.Front.controllers;

import lombok.RequiredArgsConstructor;
import modulos.Front.dtos.output.ColeccionOutputDTO;
import modulos.Front.services.ColeccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/colecciones")
@RequiredArgsConstructor
public class ColeccionController {

    private final ColeccionService coleccionService;

    @GetMapping("/get-all")
    public String obtenerTodasLasColecciones(Model model){
        ResponseEntity<?> rta = coleccionService.obtenerTodasLasColecciones();
        ColeccionOutputDTO dto = (ColeccionOutputDTO)rta.getBody();
        if (dto!=null)
            System.out.println("SOY UNA COLECCION DE TITULO: " + dto.getTitulo());
        else
            System.out.println("SOY UNA MIERDA DE PERSONA");
        return "PINGA";
    }

}
