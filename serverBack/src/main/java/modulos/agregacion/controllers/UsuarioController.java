package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.UsuarioService;
import modulos.shared.dtos.input.*;
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

    // Anda
    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioInputDTO dtoInput){
        return usuarioService.crearUsuario(dtoInput);
    }

    // Anda
    @PostMapping("/iniciar-sesion")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginDtoInput login){
        return usuarioService.iniciarSesion(login);
    }

    // Anda
    @PostMapping("/editar/contrasenia")
    public ResponseEntity<?> cambiarContrasenia(@RequestBody CambiarContraseniaDtoInput dtoImput){
        return usuarioService.cambiarContrasenia(dtoImput);
    }

    // Anda
    @PostMapping("/editar/campos-escalares")
    public ResponseEntity<?> editarUsuario(@RequestBody EditarUsuarioDtoInput dtoImput){
        return usuarioService.editarUsuario(dtoImput);
    }

    // Anda
    @PostMapping("/editar/nombre-usuario")
    public ResponseEntity<?> editarNombreDeUsuario(@RequestBody EditarNombreDeUsuarioDtoInput dtoImput){
        return usuarioService.editarNombreDeUsuario(dtoImput);
    }

    // Anda
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@RequestParam Long id){
        return usuarioService.getAll(id);
    }

    @GetMapping("/get/usuario/{nombre_usuario}")
    public ResponseEntity<?> getUsuarioByNombreUsuario(@RequestParam String nombre_usuario){
        return usuarioService.getUsuarioByNombreUsuario(nombre_usuario);
    }

}