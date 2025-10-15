package modulos.Front.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.input.*;
import modulos.Front.dtos.output.MensajeOutputDTO;
import modulos.Front.dtos.output.SolicitudHechoOutputDTO;
import modulos.Front.services.SolicitudHechoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/solicitudes-hecho")
@RequiredArgsConstructor
public class SolicitudHechoController {
    private final SolicitudHechoService solicitudHechoService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/evaluar/subir")
    public String evaluarSolicitudSubida(@Valid SolicitudHechoEvaluarInputDTO dtoInput, RedirectAttributes ra){
        ResponseEntity<?> rta = solicitudHechoService.evaluarSolicitudSubida(dtoInput);

        if (rta.getStatusCode().is2xxSuccessful()) {
            return "solicitudes";
        }
        return "redirect:/" + rta.getStatusCode().value();

    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/evaluar/eliminar")
    public String evaluarSolicitudEliminacion(@Valid @ModelAttribute SolicitudHechoEvaluarInputDTO dto, RedirectAttributes ra){
        ResponseEntity<?> rta = solicitudHechoService.evaluarSolicitudEliminacion(dto);

        if(rta.getStatusCode().is2xxSuccessful()){
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/evaluar/modificar")
    public String evaluarSolicitudModificacion(@Valid @ModelAttribute SolicitudHechoModificarInputDTO dto, RedirectAttributes ra){
        ResponseEntity<?> rta = solicitudHechoService.evaluarSolicitudModificacion(dto);

        if(rta.getStatusCode().is2xxSuccessful()){
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE', 'VISUALIZADOR')")
    @PostMapping("/subir-hecho")
    public String enviarSolicitudSubirHecho(@Valid @ModelAttribute SolicitudHechoInputDTO dto, RedirectAttributes ra){
        ResponseEntity<?> rta = this.solicitudHechoService.enviarSolicitudSubirHecho(dto);

        if(rta.getStatusCode().is2xxSuccessful()){
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE', 'VISUALIZADOR')")
    @GetMapping("/get-mensajes")
    public String getMensajesUsuario(@Valid @ModelAttribute Long id_receptor, Model model, RedirectAttributes ra){
        ResponseEntity<?> rta = solicitudHechoService.obtenerMensajes(id_receptor);

        if(rta.getStatusCode().is2xxSuccessful()){
            List<MensajeOutputDTO> mensajes = BodyToListConverter.bodyToList(rta, MensajeOutputDTO.class);
            model.addAttribute("mensajes", mensajes);
            return "mensajes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE', 'VISUALIZADOR')")
    @PostMapping("/reportes/reportar")
    public String reportar(@Valid @RequestParam Long id_hecho, @Valid @RequestParam String fuente, @Valid @RequestParam String motivo, RedirectAttributes ra){

        ResponseEntity<?> rta = solicitudHechoService.reportarHecho(motivo, id_hecho, fuente);

        if(rta.getStatusCode().is2xxSuccessful()){
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }


    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE', 'VISUALIZADOR')")
    @PostMapping("/eliminar-hecho")
    public String enviarSolicitudEliminarHecho(@Valid @ModelAttribute SolicitudHechoEliminarInputDTO dto, RedirectAttributes ra){
        ResponseEntity<?> rta = this.solicitudHechoService.enviarSolicitudEliminarHecho(dto);

        if(rta.getStatusCode().is2xxSuccessful()){
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE', 'VISUALIZADOR')")
    @PostMapping("/eliminar-hecho")
    public String enviarSolicitudModificarHecho(@Valid @ModelAttribute SolicitudHechoEliminarInputDTO dto, RedirectAttributes ra){
        ResponseEntity<?> rta = this.solicitudHechoService.enviarSolicitudModificarHecho(dto);

        if(rta.getStatusCode().is2xxSuccessful()){
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/reportes/get/all")
    public String getAllReportes(@RequestParam Long id_usuario, Model model, RedirectAttributes ra) {
        ResponseEntity<?> rta = this.solicitudHechoService.getAllReportes(id_usuario);

        if(rta.getStatusCode().is2xxSuccessful()){
            return "solicitudes";
        }
        else if(rta.getBody() != null) {
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/reportes/evaluar")
    public String evaluarReporte(@Valid @ModelAttribute EvaluarReporteInputDTO dtoInput, RedirectAttributes ra){
        ResponseEntity<?> rta = solicitudHechoService.evaluarReporte(dtoInput);

        if (rta.getStatusCode().is2xxSuccessful()) {
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    // Anda
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/get/all")
    public String getAllSolicitudes(@RequestParam Long id_usuario, Model model, RedirectAttributes ra){
        ResponseEntity<?> rta = solicitudHechoService.getAllSolicitudes(id_usuario);

        if (rta.getStatusCode().is2xxSuccessful()) {
            List<SolicitudHechoOutputDTO> solicitudes = BodyToListConverter.bodyToList(rta, SolicitudHechoOutputDTO.class);
            model.addAttribute("solicitudes", solicitudes);
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/get/pendientes")
    public String getSolicitudesPendientes(@Valid @RequestParam Long id_usuario, Model model, RedirectAttributes ra) {
        ResponseEntity<?> rta = solicitudHechoService.getSolicitudesPendientes(id_usuario);

        if (rta.getStatusCode().is2xxSuccessful()) {
            List<SolicitudHechoOutputDTO> solicitudes = BodyToListConverter.bodyToList(rta, SolicitudHechoOutputDTO.class);
            model.addAttribute("solicitudes", solicitudes);
            return "solicitudes";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

/*

🧩 1. Model — datos para la misma vista (sin redirect)

Usás el Model cuando estás devolviendo directamente una vista (HTML) en el mismo request.

🔹 Los datos del Model se pierden si hacés un redirect.
🔹 Se usan cuando hacés algo como return "vista" (no redirect:).

Ejemplo:

@GetMapping("/form")
public String mostrarFormulario(Model model) {
    model.addAttribute("usuario", new UsuarioDTO());
    return "formulario"; // Se muestra la vista "formulario.html"
}


👉 Se usa para renderizar datos en el mismo renderizado de la vista, típico de un GET.


    (a) addAttribute()

👉 Agrega datos como parámetros en la URL (/destino?key=value).

    @PostMapping("/procesar")
    public String procesar(RedirectAttributes ra) {
        ra.addAttribute("id", 42);
        return "redirect:/detalle"; // redirige a /detalle?id=42
    }

    (b) addFlashAttribute()

👉 Agrega datos que no van en la URL, se guardan temporalmente en sesión y se eliminan luego del redirect.

    @PostMapping("/crear")
    public String crear(@Valid FormDTO dto, RedirectAttributes ra) {
        ra.addFlashAttribute("mensaje", "Se creó correctamente");
        ra.addFlashAttribute("tipo", "success");
        return "redirect:/form";
    }
    */
}
