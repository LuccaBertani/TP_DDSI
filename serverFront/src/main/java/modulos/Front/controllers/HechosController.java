package modulos.Front.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.input.*;
import modulos.Front.dtos.output.AtributosModificarDTO;
import modulos.Front.dtos.output.HechosResponse;
import modulos.Front.dtos.output.UsuarioOutputDto;
import modulos.Front.dtos.output.VisualizarHechosOutputDTO;
import modulos.Front.services.HechosService;
import modulos.Front.services.SolicitudHechoService;
import modulos.Front.services.UsuarioService;
import modulos.Front.usuario.Rol;
import modulos.Front.usuario.Usuario;
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
    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SolicitudHechoService solicitudHechoService;

    @PreAuthorize("isAuthenticated()") // Solo usuarios logueados
    @GetMapping("/mis-hechos")
    public String getHechosDelUsuario(Model model){
        System.out.println("ENTRO A GET HECHOS DEL USUARIO (Front)");

        ResponseEntity<?> rtaDto = this.hechosService.getHechosDelUsuario();

        if(rtaDto.getStatusCode().is2xxSuccessful() && rtaDto.getBody() != null){
            List<VisualizarHechosOutputDTO> hechos = BodyToListConverter.bodyToList(rtaDto, VisualizarHechosOutputDTO.class);
            model.addAttribute("listaHechos", hechos);
            return "hecho";
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

        return "redirect:/" + rtaDto.getStatusCode().value();
    }


    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/subir")
    public String subirHecho(RedirectAttributes ra, @Valid @ModelAttribute SolicitudHechoInputDTO hechoInputDTO){
        ResponseEntity<?> rtaDto = this.hechosService.subirHecho(hechoInputDTO);
        if(rtaDto.getStatusCode().is2xxSuccessful()){
            return "redirect:/public/contribuir";
        }
        else if(rtaDto.getBody() != null){
            ra.addFlashAttribute(rtaDto.getBody().toString());
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/importar")
    public String importar(Model model){
        model.addAttribute("meta", new ImportacionHechosInputDTO());
        return "importarCsv";
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/importar")
    public String importarHechos(@Valid @RequestPart("meta") ImportacionHechosInputDTO dtoInput,
                                 @RequestPart("file") MultipartFile file, RedirectAttributes ra){

        System.out.println("ENTRE A IMPORTAR CON ESTO: " + dtoInput.getFuenteString());

        ResponseEntity <?> rtaDto = this.hechosService.importarHechos(dtoInput, file);

        if(rtaDto.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("msgExito", "Csv subido correctamente");
            return "redirect:importar";
        }
        else if(rtaDto.getBody() != null){
            ra.addAttribute("msgError", rtaDto.getBody().toString());
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }



    @GetMapping("/public/get-mapa")
    public String getHechosConLatitudYLongitud(Model model){
        ResponseEntity<?> rtaDto = this.hechosService.getHechosConLatitudYLongitud();

        if (rtaDto.getStatusCode().is2xxSuccessful() && rtaDto.getBody() != null) {
            List<VisualizarHechosOutputDTO> hechos =
                    BodyToListConverter.bodyToList(rtaDto, VisualizarHechosOutputDTO.class);

            // Serializamos a JSON para evitar problemas de Thymeleaf con objetos complejos
            String hechosJson;
            try {
                hechosJson = objectMapper.writeValueAsString(hechos);
            } catch (Exception e) {
                hechosJson = "[]";
            }

            model.addAttribute("hechosJson", hechosJson);
            model.addAttribute("SolicitudHechoInputDTO", new SolicitudHechoInputDTO());
            return "mapa"; // nombre del template
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }

    @GetMapping("/public/get")
    public String getHecho(Model model, Long id_hecho, String fuente,
    @RequestParam(name = "id_solicitud", required = false) Long id_solicitud){
        ResponseEntity<?> rtaDto = this.hechosService.getHecho(id_hecho, fuente);
        System.out.println("ID DE SOLICITUD DEL CULO: " + id_solicitud);
        if(rtaDto.getStatusCode().is2xxSuccessful() && rtaDto.getBody() != null){
            VisualizarHechosOutputDTO hecho = (VisualizarHechosOutputDTO) rtaDto.getBody();
            model.addAttribute("hecho", hecho);


            model.addAttribute("puedeReportar", false);


            boolean esContribuyenteDelHecho = false;


            String usuarioActual = usuarioService.getUsernameFromSession();

            if (usuarioActual != null){

                if (hecho.getUsername() != null){
                    esContribuyenteDelHecho = hecho.getUsername().equals(usuarioActual);
                }

                if (esContribuyenteDelHecho) {

                    SolicitudHechoEliminarInputDTO solicitudHechoEliminarInputDTO = new SolicitudHechoEliminarInputDTO();
                    solicitudHechoEliminarInputDTO.setId_hecho(hecho.getId());
                    model.addAttribute("SolicitudHechoEliminarInputDTO", solicitudHechoEliminarInputDTO);


                    System.out.println("ID DEL HECHO DE RE MIL MIERDA: " + hecho.getId());

                    SolicitudHechoModificarInputDTO dtoModificar = SolicitudHechoModificarInputDTO.builder()
                            .id_hecho(hecho.getId())
                            .titulo(hecho.getTitulo())
                            .descripcion(hecho.getDescripcion())
                            .latitud(hecho.getLatitud())
                            .longitud(hecho.getLongitud())
                            .fechaAcontecimiento(hecho.getFechaAcontecimiento())
                            .id_pais(hecho.getId_pais())
                            .id_provincia(hecho.getId_provincia())
                            .id_categoria(hecho.getId_categoria())
                            .build();

                    model.addAttribute("SolicitudHechoModificarInputDTO", dtoModificar);



                }
                else {

                    if (id_solicitud != null){
                        System.out.println("ID SOLICITUD: " + id_solicitud);
                        ResponseEntity<?> rtaAtributosModificar = solicitudHechoService.getAtributosHechoAModificar(id_solicitud);
                        if (rtaAtributosModificar.getStatusCode().is2xxSuccessful() && rtaAtributosModificar.hasBody()){
                            AtributosModificarDTO atributosModificarDTO = (AtributosModificarDTO) rtaAtributosModificar.getBody();
                            System.out.println("TITULO A MODIFICAR: " + atributosModificarDTO.getTitulo());
                            model.addAttribute("AtributosModificarDTO", atributosModificarDTO);
                        }
                    }

                    model.addAttribute("puedeReportar", true);
                }
            }
            else {
                model.addAttribute("puedeReportar", true);
            }

            return "detalleHecho";
        }

        else if (rtaDto.getBody() != null){
            model.addAttribute("errorMsg", rtaDto.getBody().toString());
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }


    @PostMapping("/public/get/filtrar")
    public String getHechosFiltradosColeccion(@Valid @ModelAttribute GetHechosColeccionInputDTO inputDTO, Model model){

        inputDTO.setOrigenConexion(0);

        ResponseEntity<?> rtaDto = this.hechosService.getHechosFiltradosColeccion(inputDTO);

        if(rtaDto.getStatusCode().is2xxSuccessful() && rtaDto.getBody() != null){
            List<VisualizarHechosOutputDTO> hechos = BodyToListConverter.bodyToList(rtaDto, VisualizarHechosOutputDTO.class);

            if(hechos != null) {
                for (VisualizarHechosOutputDTO hecho : hechos) {
                    System.out.println("Titulo de hecho: " + hecho.getTitulo());
                }

                model.addAttribute("listaHechos", hechos);
                return "hecho";
            }
        }
        return "redirect:/" + rtaDto.getStatusCode().value();
    }




    //todo falta

}
