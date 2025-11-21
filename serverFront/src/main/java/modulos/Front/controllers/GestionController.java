package modulos.Front.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import modulos.Front.dtos.input.CategoriaInputDTO;
import modulos.Front.dtos.input.SinonimoInputDTO;
import modulos.Front.services.GestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
@AllArgsConstructor
public class GestionController {

    private final GestionService gestionService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
    @GetMapping("/categorias/crear")
    public String cargarFormCrearCategoria(Model model){
        model.addAttribute("categoriaForm", new CategoriaInputDTO());
        return "crearCategoria";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
    @PostMapping("/categorias")
    public String crearCategoria(@Valid @ModelAttribute("categoriaForm") CategoriaInputDTO dtoInput, RedirectAttributes ra) {

        ResponseEntity<?> rta = this.gestionService.crearCategoria(dtoInput);

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

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
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
    }
}