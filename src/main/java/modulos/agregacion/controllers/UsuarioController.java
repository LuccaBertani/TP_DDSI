package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.UsuarioService;
import modulos.shared.dtos.input.*;
import modulos.agregacion.entities.RespuestaHttp;
import modulos.agregacion.entities.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearUsuario(@Valid @RequestBody UsuarioInputDTO dtoInput){
        RespuestaHttp<Usuario> respuesta = usuarioService.crearUsuario(dtoInput);
        Integer codigo = respuesta.getCodigo();
        if (codigo.equals(HttpStatus.BAD_REQUEST.value())){
            return ResponseEntity.status(codigo).body("No tenes permiso!");
        }

        return ResponseEntity.status(codigo).body("El usuario se creó correctamente");
    }

    @PostMapping("/iniciarSesion")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginDtoInput login){
        return usuarioService.iniciarSesion(login);
    }

    @PostMapping("/editar/contrasenia")
    public ResponseEntity<?> cambiarContrasenia(@RequestBody CambiarContraseniaDtoInput dtoImput){
        return usuarioService.cambiarContrasenia(dtoImput);
    }

    @PostMapping("/editar/camposEscalares")
    public ResponseEntity<?> editarUsuario(@RequestBody EditarUsuarioDtoInput dtoImput){
        return usuarioService.editarUsuario(dtoImput);
    }

    @PostMapping("/editar/nombreDeUsuario")
    public ResponseEntity<?> editarNombreDeUsuario(@RequestBody EditarNombreDeUsuarioDtoInput dtoImput){
        return usuarioService.editarNombreDeUsuario(dtoImput);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@RequestParam Long id){
        return usuarioService.getAll(id);
    }

}