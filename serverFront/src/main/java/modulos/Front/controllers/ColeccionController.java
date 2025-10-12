package modulos.Front.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.output.ColeccionOutputDTO;
import modulos.Front.services.ColeccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.Authenticator;
import java.util.List;


@Controller
@RequestMapping("/colecciones")
@RequiredArgsConstructor
public class ColeccionController {

    private final ColeccionService coleccionService;

    // TODO VISTA
    @GetMapping("/get-all")
    @PreAuthorize("hasAnyRole('VISUALIZADOR', 'CONTRIBUYENTE', 'ADMINISTRADOR')")
    public String obtenerTodasLasColecciones(Model model, Authenticator authentication){
        ResponseEntity<?> rta = coleccionService.obtenerTodasLasColecciones();

        if (rta.getStatusCode().equals(HttpStatus.OK) && rta.getBody() != null){
            List<ColeccionOutputDTO> colecciones = BodyToListConverter.bodyToList(rta, ColeccionOutputDTO.class);
            model.addAttribute("colecciones", colecciones);
            model.addAttribute("titulo", "Listado de colecciones");

        }

        return "PINGA";
    }

    // Prueba de conexión entre el server front y el server back
    @GetMapping("/get-all")
    public ResponseEntity<?> obtenerTodasLasColecciones(){
        return coleccionService.obtenerTodasLasColecciones();
    }

}
