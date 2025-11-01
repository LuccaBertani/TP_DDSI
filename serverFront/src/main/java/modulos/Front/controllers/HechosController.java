package modulos.Front.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.input.GetHechosColeccionInputDTO;
import modulos.Front.dtos.input.ImportacionHechosInputDTO;
import modulos.Front.dtos.input.SolicitudHechoInputDTO;
import modulos.Front.dtos.input.UsuarioInputDTO;
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


    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/subir")
    public String subirHecho(RedirectAttributes ra, @Valid @ModelAttribute SolicitudHechoInputDTO hechoInputDTO){

        ResponseEntity<?> rtaDto = this.hechosService.subirHecho(hechoInputDTO);

        if(rtaDto.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("msgExito", "Coleccion creada correctamente");
            return "redirect:crear";
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
            ra.addFlashAttribute("msgExito", "Csv subido correctamente");
            return "redirect:crear";
        }
        else if(rtaDto.getBody() != null){
            ra.addAttribute("msgError", rtaDto.getBody().toString());
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }

    @GetMapping("/public/get-all")
    public String getHechos(Model model){
        System.out.println("ENTRO A GET ALL HECHOS");
        ResponseEntity<?> rtaDto = this.hechosService.getHechos();

        if(rtaDto.getStatusCode().is2xxSuccessful() && rtaDto.getBody() != null){
            List<VisualizarHechosOutputDTO> hechos = BodyToListConverter.bodyToList(rtaDto, VisualizarHechosOutputDTO.class);
            model.addAttribute("listaHechos", hechos);
            return "hecho";
        }
        /*else if (rtaDto.getBody() != null){
            model.addAttribute("errorMsg", rtaDto.getBody().toString());
        }*/
        return "redirect:/" + rtaDto.getStatusCode().value();
    }

    //@PreAuthorize("hasAnyRole('VISUALIZADOR', 'CONTRIBUYENTE', 'ADMINISTRADOR')")
    @GetMapping("/public/get")
    public String getHecho(Model model, Long id_hecho, String fuente){
        ResponseEntity<?> rtaDto = this.hechosService.getHecho(id_hecho, fuente);

        System.out.println("HOLA");

        if(rtaDto.getStatusCode().is2xxSuccessful() && rtaDto.getBody() != null){
            VisualizarHechosOutputDTO hecho = (VisualizarHechosOutputDTO) rtaDto.getBody();
            System.out.println("Fecha acontecimiento de x: " + hecho.getFechaAcontecimiento());
            System.out.println("Fecha carga de x: " + hecho.getFechaCarga());
            model.addAttribute("hecho", hecho);
            return "detalleHecho";
        }
        else if (rtaDto.getBody() != null){
            model.addAttribute("errorMsg", rtaDto.getBody().toString());
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE', 'VISUALIZADOR')")
    @PostMapping("/get/filtrar")
    public String getHechosFiltradosColeccion(@Valid @ModelAttribute GetHechosColeccionInputDTO inputDTO, Model model){
        System.out.println("Hola entré acá, navegacion curada: " + inputDTO.getNavegacionCurada());
        System.out.println("id de coleccion: " + inputDTO.getId_coleccion());

        inputDTO.setOrigenConexion(0);
        System.out.println("origen conexion: " + inputDTO.getOrigenConexion());

        ResponseEntity<?> rtaDto = this.hechosService.getHechosFiltradosColeccion(inputDTO);

        if(rtaDto.getStatusCode().is2xxSuccessful() && rtaDto.getBody() != null){
            List<VisualizarHechosOutputDTO> hechos = BodyToListConverter.bodyToList(rtaDto, VisualizarHechosOutputDTO.class);

            for (VisualizarHechosOutputDTO hecho: hechos){
                System.out.println("Titulo de hecho: " + hecho.getTitulo());
            }

            model.addAttribute("listaHechos", hechos);
            return "hecho";
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }

    //todo falta



}
