/*package modulos.Front.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modulos.Front.dtos.output.CategoriaDto;
//import modulos.Front.services.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    //private final CategoriaService categoriaService;

    @GetMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String getCrear(Model model){
        model.addAttribute("titulo", "Crear Categoría — MetaMapa");
        if (!model.containsAttribute("categoriaForm")) {
            model.addAttribute("categoriaForm", new CategoriaDto());
        }
        return "categorias-crear"; // => tu template nuevo
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String crear(@Valid @ModelAttribute CategoriaDto categoriaForm,
                        RedirectAttributes ra){
        /*ResponseEntity<?> rta = categoriaService.crearCategoria(categoriaForm);
        if (rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("mensaje", "Categoría creada correctamente");
            ra.addFlashAttribute("tipo", "success");
            return "redirect:/categorias/crear";
        }*//*
        ra.addFlashAttribute("mensaje", "No se pudo crear la categoría");
        ra.addFlashAttribute("tipo", "danger");
        ra.addFlashAttribute("categoriaForm", categoriaForm);
        return "redirect:/categorias/crear";
    }
}
*/
