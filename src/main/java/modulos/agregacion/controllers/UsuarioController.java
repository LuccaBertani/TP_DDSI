package modulos.agregacion.controllers;

import jakarta.validation.Valid;
import modulos.agregacion.services.UsuarioService;
import modulos.shared.dtos.input.UsuarioInputDTO;
import modulos.shared.RespuestaHttp;
import modulos.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearUsuario(@Valid @RequestBody UsuarioInputDTO dtoInput){
        RespuestaHttp<Usuario> respuesta = usuarioService.crearUsuario(dtoInput);
        Integer codigo = respuesta.getCodigo();
        if (codigo.equals(HttpStatus.BAD_REQUEST.value())){
            return ResponseEntity.status(codigo).build();
        }

        return ResponseEntity.status(codigo).body("El usuario se creó correctamente"); // Asumo 200 OK
    }

}