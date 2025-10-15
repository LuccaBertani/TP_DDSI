package modulos.Front.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.input.GetHechosColeccionInputDTO;
import modulos.Front.dtos.input.ImportacionHechosInputDTO;
import modulos.Front.dtos.input.SolicitudHechoInputDTO;
import modulos.Front.dtos.output.HechosResponse;
import modulos.Front.dtos.output.VisualizarHechosOutputDTO;
import modulos.Front.services.HechosService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/hechos")
@RequiredArgsConstructor
public class HechosController {
    private final HechosService hechosService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE', 'VISUALIZADOR')")
    @PostMapping("/crear")
    public String crearHecho(RedirectAttributes ra, @Valid @ModelAttribute SolicitudHechoInputDTO hechoInputDTO){
        ResponseEntity<?> rtaDto = this.hechosService.crearHecho(hechoInputDTO);

        if(rtaDto.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("msgExito", "Coleccion creada correctamente");
            return "redirect:/crear";
        }
        else if(rtaDto.getBody() != null){
            ra.addAttribute("msgError", rtaDto.getBody().toString());
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/importar")
    public String importarHechos(@Valid @RequestPart("meta") ImportacionHechosInputDTO dtoInput,
                                 @RequestPart("file") MultipartFile file, RedirectAttributes ra){
        ResponseEntity <?> rtaDto = this.hechosService.importarHechos(dtoInput, file);

        if(rtaDto.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("msgExito", "Coleccion creada correctamente");
            return "redirect:/importar";
        }
        else if(rtaDto.getBody() != null){
            ra.addAttribute("msgError", rtaDto.getBody().toString());
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE', 'VISUALIZADOR')")
    @GetMapping("/get-all")
    public String getHechos(Model model){
        ResponseEntity<?> rtaDto = this.hechosService.getHechos();

        if(rtaDto.getStatusCode().is2xxSuccessful() && rtaDto.getBody() != null){
            HechosResponse hechos = (HechosResponse) rtaDto.getBody();
            model.addAttribute("listaHechos", hechos.getHechos());
            return "dashboard";
        }
        else if (rtaDto.getBody() != null){
            model.addAttribute("errorMsg", rtaDto.getBody().toString());
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE', 'VISUALIZADOR')")
    @PostMapping("/get/filtrar")
    public String getHechosFiltradosColeccion(@Valid @ModelAttribute GetHechosColeccionInputDTO inputDTO, Model model){
        ResponseEntity<?> rtaDto = this.hechosService.getHechosFiltradosColeccion(inputDTO);

        if(rtaDto.getStatusCode().is2xxSuccessful() && rtaDto.getBody() != null){
            List<VisualizarHechosOutputDTO> hechos = BodyToListConverter.bodyToList(rtaDto, VisualizarHechosOutputDTO.class);
            model.addAttribute("listaHechos", hechos);
            return "dashboard";
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }

    //todo falta

}
