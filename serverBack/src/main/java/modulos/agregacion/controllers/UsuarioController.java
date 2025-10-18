package modulos.agregacion.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import modulos.agregacion.entities.DbMain.usuario.Rol;
import modulos.agregacion.entities.DbMain.usuario.Usuario;
import modulos.agregacion.services.UsuarioService;
import modulos.shared.dtos.input.TokenResponse;
import modulos.shared.dtos.input.*;
import modulos.shared.dtos.output.AuthResponseDTO;
import modulos.shared.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PostMapping("/public/crear")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioInputDTO dtoInput){
        System.out.println("HOLA SOY UN ESTORBIN Y QUIERO CREAR USUARIO EN EL BACK");
        return usuarioService.crearUsuario(dtoInput);
    }

    // Anda
    @PostMapping("/editar/contrasenia")
    public ResponseEntity<?> cambiarContrasenia(@RequestBody CambiarContraseniaDtoInput dtoImput, @AuthenticationPrincipal Jwt principal){
        return usuarioService.cambiarContrasenia(dtoImput, principal);
    }

    // Anda
    @PostMapping("/editar/campos-escalares")
    public ResponseEntity<?> editarUsuario(@RequestBody EditarUsuarioDtoInput dtoImput, @AuthenticationPrincipal Jwt principal){
        return usuarioService.editarUsuario(dtoImput, principal);
    }

    // Anda
    @PostMapping("/editar/nombre-usuario")
    public ResponseEntity<?> editarNombreDeUsuario(@RequestBody EditarNombreDeUsuarioDtoInput dtoImput, @AuthenticationPrincipal Jwt principal){
        return usuarioService.editarNombreDeUsuario(dtoImput, principal);
    }

    // Anda
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@AuthenticationPrincipal Jwt principal){
        return usuarioService.getAll(principal);
    }

    @GetMapping("/get/usuario")
    public ResponseEntity<?> getUsuarioByNombreUsuario(@AuthenticationPrincipal Jwt principal){
        return usuarioService.getUsuarioByNombreUsuario(principal);
    }

    // Anda
    @GetMapping("/get-mensajes")
    public ResponseEntity<?> getMensajesUsuario(@AuthenticationPrincipal Jwt principal){
        return usuarioService.obtenerMensajes(principal);
    }

    @PostMapping("/auth")
    public ResponseEntity<?> loginApi(@RequestBody LoginDtoInput dtoInput) {

        String username = dtoInput.getNombreDeUsuario();
        String password = dtoInput.getContrasenia();

        // Validación básica de credenciales
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Autenticar usuario usando el LoginService
        ResponseEntity<?> rta = usuarioService.iniciarSesion(username, password);

        if (!rta.getStatusCode().is2xxSuccessful()){
            return rta;
        }

        Rol rolUsuario = (Rol)rta.getBody();

        // Generar tokens
        String accessToken = JwtUtil.generarAccessToken(username);
        String refreshToken = JwtUtil.generarRefreshToken(username);

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .rol(rolUsuario)
                .build();


        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenResponse request) {
        try {

            Claims claims = JwtUtil.parseClaims(request.getRefreshToken());

            if (!"refresh".equals(claims.get("type"))) {
                return ResponseEntity.badRequest().build();
            }


            ResponseEntity<?> rta = usuarioService.getUsuarioByNombreUsuario(claims.getSubject());

            if (!rta.getStatusCode().is2xxSuccessful()){
                return rta;
            }
            Usuario usuario = (Usuario) rta.getBody();
            Rol rol = usuario.getRol();
            String newAccessToken = JwtUtil.generarAccessToken(claims.getSubject());
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(request.getRefreshToken())
                    .rol(rol)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



}