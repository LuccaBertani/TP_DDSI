package modulos.agregacion.controllers;

import io.jsonwebtoken.Claims;
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

    @GetMapping("/get/usuario")
    public ResponseEntity<?> getUsuarioByNombreUsuario(@RequestParam String nombre_usuario){
        return usuarioService.getUsuarioByNombreUsuario(nombre_usuario);
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

        Usuario usuario = (Usuario)rta.getBody();

        // Generar tokens
        String accessToken = JwtUtil.generarAccessToken(username, usuario.getRol());
        String refreshToken = JwtUtil.generarRefreshToken(username, usuario.getRol());

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .rol(usuario.getRol())
                .build();


        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenResponse request) {
        try {
            Claims claimsRequest = JwtUtil.parseClaims(request.getRefreshToken());

            // Validar que el token sea de tipo refresh
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey())
                    .build()
                    .parseClaimsJws(request.getRefreshToken())
                    .getBody();

            if (!"refresh".equals(claims.get("type"))) {
                return ResponseEntity.badRequest().build();
            }

            ResponseEntity<?> rta = usuarioService.getUsuarioByNombreUsuario(claimsRequest.getSubject());

            if (!rta.getStatusCode().is2xxSuccessful()){
                return rta;
            }

            // OJO CON LOS CASOS DE CAMBIO DE ROL VISUALIZADOR <-> CONTRIBUYENTE: Yo asumo que se modifica antes al rol del usuario y que se guarda en la bdd
            Rol rol = claims.get("rol", Rol.class);
            String newAccessToken = JwtUtil.generarAccessToken(claimsRequest.getSubject(), rol);
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