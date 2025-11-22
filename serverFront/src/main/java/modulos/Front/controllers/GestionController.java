package modulos.Front.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.input.SinonimoInputDTO;
import modulos.Front.dtos.output.CategoriaDto;
import modulos.Front.dtos.output.PaisDto;
import modulos.Front.dtos.output.PaisProvinciaDTO;
import modulos.Front.dtos.output.ProvinciaDto;
import modulos.Front.services.CategoriaService;
import modulos.Front.services.HechosService;
import modulos.Front.services.SinonimoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/gestion")
public class GestionController {

    private final CategoriaService categoriaService;
    private final SinonimoService sinonimoService;
    private final HechosService hechosService;

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
    public String cargarFormCrearSinonimo(
            Model model,
            @ModelAttribute("sinonimoForm") SinonimoInputDTO sinonimoInputDTO
    ) {
        // Si entro por primera vez y no hay form en el modelo
        if (!model.containsAttribute("sinonimoForm")) {
            model.addAttribute("sinonimoForm", new SinonimoInputDTO());
        }

        ResponseEntity<?> rtaPaises = hechosService.getPaises();
        ResponseEntity<?> rtaCategorias = hechosService.getCategorias();

        if (!rtaPaises.getStatusCode().is2xxSuccessful() || !rtaCategorias.getStatusCode().is2xxSuccessful()) {
            return "redirect:/404";
        }

        List<PaisDto> paises = BodyToListConverter.bodyToList(rtaPaises, PaisDto.class);
        List<CategoriaDto> categorias = BodyToListConverter.bodyToList(rtaCategorias, CategoriaDto.class);

        model.addAttribute("paises", paises);
        model.addAttribute("categorias", categorias);

        // Las provincias las vamos a traer por AJAX según el país elegido
        return "crearSinonimo";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
    @PostMapping("/sinonimos/crear")
    public String crearSinonimo(@Valid @ModelAttribute("sinonimoForm") SinonimoInputDTO dtoInput, RedirectAttributes ra) {
        ResponseEntity<?> rta = null;
        if (dtoInput.getTipo().equals("categoria")){
            rta = this.categoriaService.addSinonimo(dtoInput.getSinonimo(), dtoInput.getId_entidad());
        }

        if (rta!=null){
            if(rta.getStatusCode().is2xxSuccessful()){
                ra.addFlashAttribute("msgExito", "Sinónimo creado correctamente");
                return "redirect:/usuarios/perfil";
            } else if(rta.getBody() != null){
                ra.addFlashAttribute("msgError", rta.getBody().toString());
            }
            return "redirect:/" + rta.getStatusCode().value();
        }

        return "redirect:/404";

    }
}