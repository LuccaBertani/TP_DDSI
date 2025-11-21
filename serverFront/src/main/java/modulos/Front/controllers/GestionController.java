package modulos.Front.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import modulos.Front.dtos.input.SinonimoInputDTO;
import modulos.Front.services.CategoriaService;
import modulos.Front.services.SinonimoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@RequestMapping("/gestion")
public class GestionController {

    private final CategoriaService categoriaService;
    private final SinonimoService sinonimoService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
    @GetMapping("/categorias/crear")
    public String cargarFormCrearCategoria(){
        return "crearCategoria";
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/categorias/crear")
    public String crearCategoria(@RequestParam String categoria, RedirectAttributes ra) {

        System.out.println("SOY LA CATEGORIA DE TITULO: " + categoria);

        ResponseEntity<?> rta = this.categoriaService.crearCategoria(categoria);

        if(rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("msgExito", "Categoría creada correctamente");
            return "redirect:/usuarios/perfil";
        } else if(rta.getBody() != null){
            ra.addFlashAttribute("msgError", rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }



    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
    @GetMapping("/sinonimos/crear")
    public String cargarFormCrearSinonimo(Model model){
        model.addAttribute("sinonimoForm", new SinonimoInputDTO());
        return "crearSinonimo";
    }

    /*@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
    @PostMapping("/sinonimos")
    public String crearSinonimo(@Valid @ModelAttribute("sinonimoForm") SinonimoInputDTO dtoInput, RedirectAttributes ra) {

        ResponseEntity<?> rta = this.gestionService.crearSinonimo(dtoInput);

        if(rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("msgExito", "Sinónimo creado correctamente");
            return "redirect:/usuarios/perfil";
        } else if(rta.getBody() != null){
            ra.addFlashAttribute("msgError", rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }*/
}