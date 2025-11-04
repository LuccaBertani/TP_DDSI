/*package modulos.Front.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modulos.Front.dtos.input.SinonimoDTO;
import modulos.Front.dtos.output.SinonimoOutputDto;
import modulos.Front.services.SinonimoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sinonimos")
@RequiredArgsConstructor
public class SinonimoController {

    private final SinonimoService sinonimoService;

    // Listado (soporta filtros ?tipo= & ?q= & ?page=)
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String listar(@RequestParam(required = false) String tipo,
                         @RequestParam(required = false, name = "q") String query,
                         @RequestParam(defaultValue = "1") int page,
                         Model model){
        int pageSize = 10;

        ResponseEntity<?> rta = sinonimoService.buscar(tipo, query, page, pageSize);
        // asumimos que el service devuelve {items, page, pageTotal}
        if (rta.getStatusCode().is2xxSuccessful() && rta.getBody() != null){
            var paged = (SinonimoService.PageResult<SinonimoOutputDto>) rta.getBody();
            model.addAttribute("sinonimos", paged.items());
            model.addAttribute("page", paged.page());
            model.addAttribute("pageTotal", paged.pageTotal());
        } else {
            model.addAttribute("sinonimos", List.of());
            model.addAttribute("page", 1);
            model.addAttribute("pageTotal", 1);
        }
        // para que el template lea param.q / param.tipo
        model.addAttribute("param", new Object(){ public String q = query; public String tipo2 = tipo; });

        model.addAttribute("titulo", "Sinónimos — MetaMapa");
        return "sinonimos-listado"; // si preferís, podés usar el mismo "gestion-sinonimos"
    }

    // Form crear
    @GetMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String getCrear(Model model){
        model.addAttribute("titulo", "Crear Sinónimo — MetaMapa");
        if (!model.containsAttribute("sinonimoForm")) {
            model.addAttribute("sinonimoForm", new SinonimoDTO());
        }
        return "sinonimos-crear"; // => tu template nuevo
    }

    // Crear (recibe la lista name="sinonimos")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String crear(@Valid @ModelAttribute SinonimoDTO form,
                        @RequestParam(name = "sinonimos") List<String> sinonimos,
                        RedirectAttributes ra){
        List<String> limpios = sinonimos.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        form.setSinonimos(limpios);

        ResponseEntity<?> rta = sinonimoService.crear(form);
        if (rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("mensaje", "Sinónimo creado correctamente");
            ra.addFlashAttribute("tipo", "success");
            return "redirect:/sinonimos/crear";
        }
        ra.addFlashAttribute("mensaje", "No se pudo crear el sinónimo");
        ra.addFlashAttribute("tipo", "danger");
        ra.addFlashAttribute("sinonimoForm", form);
        return "redirect:/sinonimos/crear";
    }

    // Editar (GET)
    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String editar(@PathVariable Long id, Model model){
        ResponseEntity<?> rta = sinonimoService.getById(id);
        if (rta.getStatusCode().is2xxSuccessful() && rta.getBody()!=null){
            var dto = (SinonimoOutputDto) rta.getBody();
            model.addAttribute("sinonimo", dto);
            model.addAttribute("titulo", "Editar Sinónimo — MetaMapa");
            return "sinonimos-editar";
        }
        return "redirect:/sinonimos";
    }

    // Editar (POST)
    @PostMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute SinonimoDTO form,
                             @RequestParam(name = "sinonimos", required = false) List<String> sinonimos,
                             RedirectAttributes ra){
        List<String> limpios = (sinonimos == null ? List.<String>of() : sinonimos).stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
        form.setSinonimos(limpios);

        ResponseEntity<?> rta = sinonimoService.actualizar(id, form);
        if (rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("mensaje", "Sinónimo actualizado");
            return "redirect:/sinonimos";
        }
        ra.addFlashAttribute("mensaje", "No se pudo actualizar");
        ra.addFlashAttribute("tipo", "danger");
        return "redirect:/sinonimos/" + id + "/editar";
    }

    // Borrar (tu template usa <form method="post"><input name="_method" value="delete">)
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String borrarCompat(@PathVariable Long id,
                               @RequestParam(name="_method", required=false) String method,
                               RedirectAttributes ra){
        if ("delete".equalsIgnoreCase(method)){
            ResponseEntity<?> rta = sinonimoService.eliminar(id);
            if (rta.getStatusCode().is2xxSuccessful()){
                ra.addFlashAttribute("mensaje", "Sinónimo eliminado");
                ra.addFlashAttribute("tipo", "success");
                return "redirect:/sinonimos";
            }
            ra.addFlashAttribute("mensaje", "No se pudo eliminar");
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:/sinonimos";
        }
        return "redirect:/sinonimos";
    }
}
*/