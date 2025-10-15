package modulos.Front.controllers;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import modulos.Front.BodyToListConverter;
import modulos.Front.dtos.input.CambiarContraseniaDtoInput;
import modulos.Front.dtos.input.EditarNombreDeUsuarioDtoInput;
import modulos.Front.dtos.input.EditarUsuarioDtoInput;
import modulos.Front.dtos.input.UsuarioInputDTO;
import modulos.Front.dtos.output.UsuarioOutputDto;
import modulos.Front.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class UsuarioController {

    private UsuarioService usuarioService;

    @PostMapping("/crear")
    public String crearUsuario(@Valid @ModelAttribute UsuarioInputDTO dtoInput, RedirectAttributes ra) {
        ResponseEntity<?> rta = this.usuarioService.crearUsuario(dtoInput);

        if(rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("msgExito", "Se registró correctamente");
            return "redirect:/crear";
        }
        else if(rta.getBody() != null){
            ra.addAttribute("msgError", rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PostMapping("/editar/contrasenia")
    public String cambiarContrasenia(@Valid @ModelAttribute CambiarContraseniaDtoInput dto, RedirectAttributes ra){
        ResponseEntity<?> rta = this.usuarioService.cambiarContrasenia(dto);

        if(rta.getStatusCode().is2xxSuccessful()){
            return "editar/contrasenia";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();

    }

    @PostMapping("/editar/campos-escalares")
    public String editarUsuario(@Valid @ModelAttribute EditarUsuarioDtoInput dto, RedirectAttributes ra){
        ResponseEntity<?> rta = this.usuarioService.editarUsuario(dto);

        if(rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("msgExito", "Campos editados correctamente");
            return "redirect:/editar";
        }
        else if(rta.getBody() != null){
            ra.addAttribute("msgError", rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @PostMapping("/editar/nombre-usuario")
    public String editarNombreDeUsuario(@Valid @ModelAttribute EditarNombreDeUsuarioDtoInput dto, RedirectAttributes ra){
        ResponseEntity<?> rta = this.usuarioService.editarNombreDeUsuario(dto);

        if(rta.getStatusCode().is2xxSuccessful()){
            ra.addFlashAttribute("msgExito", "Campos editados correctamente");
            return "redirect:/editar/nombre-usuario";
        }
        else if(rta.getBody() != null){
            ra.addAttribute("msgError", rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @GetMapping("/get-all")
    public String getAll(@Valid @ModelAttribute Long id, Model model, RedirectAttributes ra){
        ResponseEntity<?> rta = usuarioService.getAll(id);

        if(rta.getStatusCode().is2xxSuccessful()){
            List<UsuarioOutputDto> usuarios = BodyToListConverter.bodyToList(rta, UsuarioOutputDto.class);

            model.addAttribute("usuarios", usuarios);
            return "usuarios";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

    @GetMapping("/get/usuario")
    public String getUsuarioByNombreUsuario(@Valid String nombre_usuario, Model model, RedirectAttributes ra){
        ResponseEntity<?> rta =  usuarioService.getUsuarioByNombreUsuario(nombre_usuario);

        if(rta.getStatusCode().is2xxSuccessful()){
            UsuarioOutputDto usuario = (UsuarioOutputDto) rta.getBody();

            model.addAttribute("usuario", usuario);
            return "usuario";
        }
        else if(rta.getBody() != null){
            ra.addFlashAttribute(rta.getBody().toString());
        }
        return "redirect:/" + rta.getStatusCode().value();
    }

}
