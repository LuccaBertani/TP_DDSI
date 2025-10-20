package modulos.Front.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.input.ColeccionInputDTO;
import modulos.Front.dtos.input.ColeccionUpdateInputDTO;
import modulos.Front.dtos.input.ModificarConsensoInputDTO;
import modulos.Front.dtos.output.ColeccionOutputDTO;
import modulos.Front.services.ColeccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/colecciones")
@RequiredArgsConstructor
public class ColeccionController {

    private final ColeccionService coleccionService;

    // Prueba de conexión entre el server front y el server back
    /*@GetMapping("/get-all")
    public ResponseEntity<?> obtenerTodasLasColecciones(){
        return coleccionService.obtenerTodasLasColecciones();
    }*/

    // http://localhost:8082/colecciones/get-all


    @GetMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String getFormularioColeccion(Model model){
        model.addAttribute("coleccionForm", new ColeccionInputDTO());
        return "gestion";
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String crearColeccion(@Valid @ModelAttribute ColeccionInputDTO inputDTO,
                                 RedirectAttributes ra) {

        ResponseEntity<?> rta = coleccionService.crearColeccion(inputDTO);

        if (rta.getStatusCode().is2xxSuccessful()) {
            ra.addFlashAttribute("mensaje", "Se creó correctamente la colección");
            return "redirect:crear";
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @GetMapping("/get-all")
    public String obtenerTodasLasColecciones(Model model){
        System.out.println("ENTRÉ A OBTENER TODAS LAS COLECCIONES");
        ResponseEntity<?> rta = coleccionService.obtenerTodasLasColecciones();

        if (rta.getStatusCode().is2xxSuccessful() && rta.getBody() != null) {
            System.out.println("SOY UN CAPO");
            List<ColeccionOutputDTO> colecciones = BodyToListConverter.bodyToList(rta, ColeccionOutputDTO.class);
            if (colecciones!=null){
                for (ColeccionOutputDTO coleccionOutputDTO : colecciones){
                    System.out.println("Coleccion de id: " + coleccionOutputDTO.getId());
                }
            }
            else{
                System.out.println("NO ENCONTRÉ COLECCIONES");
            }


            model.addAttribute("colecciones", colecciones);
            model.addAttribute("titulo", "Listado de colecciones");
            return "colecciones";
        }
        else{
            System.out.println("SOY UN ESTORBO");
        }
        return "redirect:/" + rta.getStatusCode().value();
    }


    @GetMapping("/get/{id_coleccion}")
    @PreAuthorize("hasAnyRole('VISUALIZADOR', 'CONTRIBUYENTE', 'ADMINISTRADOR')")
    public String getColeccion(@PathVariable Long id_coleccion, Model model){
        ResponseEntity<?> rta = coleccionService.getColeccion(id_coleccion);
        if (rta.getStatusCode().is2xxSuccessful() && rta.getBody() != null){
            ColeccionOutputDTO coleccion = (ColeccionOutputDTO) rta.getBody();
            model.addAttribute("coleccion", coleccion);
            return "detalleColeccion";
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String deleteColeccion(@Valid @RequestParam Long id_coleccion, RedirectAttributes ra){
        ResponseEntity<?> rta = coleccionService.deleteColeccion(id_coleccion);

        if (rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("mensaje", "Se eliminó correctamente la colección");
            ra.addFlashAttribute("tipo", "success");
            return "redirect:get-all";
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String updateColeccion(@Valid @ModelAttribute ColeccionUpdateInputDTO inputDTO, RedirectAttributes ra){
        ResponseEntity<?> rta = coleccionService.updateColeccion(inputDTO);

        if (rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("mensaje", "Se actualizó correctamente la colección");
            return "redirect:get/" + inputDTO.getId_coleccion();
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PostMapping("/add/fuente")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String agregarFuente(@Valid @RequestParam Long id_coleccion, @Valid @RequestParam String dataSet, RedirectAttributes ra){
        ResponseEntity<?> rta = coleccionService.agregarFuente(id_coleccion, dataSet);

        if (rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("mensaje", "Se agregó correctamente la fuente");
            ra.addFlashAttribute("tipo", "success");
            return "redirect:get/" + id_coleccion; // 👍
        }
        return "redirect:/" + rta.getStatusCode().value();
    }
    @PostMapping("/delete/fuente")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String eliminarFuente(@Valid @RequestParam Long id_coleccion, @Valid @RequestParam Long id_dataset, RedirectAttributes ra){
        ResponseEntity<?> rta = coleccionService.eliminarFuente(id_coleccion, id_dataset);

        if (rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("mensaje", "Se eliminó correctamente la fuente");
            ra.addFlashAttribute("tipo", "success");
            return "redirect:get/"+id_coleccion;
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PostMapping("/modificar-consenso")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String modificarAlgoritmoConsenso(@Valid @ModelAttribute ModificarConsensoInputDTO input, RedirectAttributes ra){
        ResponseEntity<?> rta = coleccionService.modificarAlgoritmoConsenso(input);

        if (rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("mensaje", "Se modificó correctamente el algoritmo de consenso asociado a la colección " + input.getIdColeccion());
            return "redirect:get/"+ input.getIdColeccion();
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PostMapping("/refrescar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String refrescarColecciones(RedirectAttributes ra){
        ResponseEntity<?> rta = coleccionService.refrescarColecciones(); // El usuario que hace el post está en el token

        if (rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("mensaje", "Se refrescaron las colecciones correctamente ");
            ra.addFlashAttribute("tipo", "success");
            return "redirect:get-all";
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

}
