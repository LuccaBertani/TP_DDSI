package raiz.controllers;

import jakarta.validation.Valid;
import raiz.models.dtos.input.UsuarioInputDTO;
import raiz.models.entities.RespuestaHttp;
import raiz.models.entities.personas.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raiz.services.IUsuarioService;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
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
