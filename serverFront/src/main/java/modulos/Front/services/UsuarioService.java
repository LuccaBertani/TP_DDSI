package modulos.Front.services;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import modulos.Front.dtos.input.*;
import modulos.Front.usuario.Usuario;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


// implements UserDetailsService

@Service
public class UsuarioService {

    private final WebApiCallerService webApiCallerService;
    private String usuarioServiceUrl = "/api/usuario";

    public UsuarioService(WebApiCallerService webApiCallerService) {
        this.webApiCallerService = webApiCallerService;
    }

    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ResponseEntity<?> rta = webApiCallerService.getEntity(usuarioServiceUrl + "/" + username, Usuario.class);

        if (!rta.getStatusCode().is2xxSuccessful()){
            throw new UsernameNotFoundException(username);
        }

        Usuario usuario = (Usuario) rta.getBody();

        List<GrantedAuthority> authorities = new ArrayList<>();

        // El rol no va a venir null. Se chequea en el back antes
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));

        return User.withUsername(usuario.getNombreDeUsuario())
                .password(usuario.getContrasenia())
                .authorities(authorities)
                .build();

        // En el camino feliz, lo que sigue es abrir la sesión con la cookie (JSESSIONID). Generar el espacio en el server para ese usuario
        // La sesion la hace spring boot
        // Se van a guardar cosas del usuario

        // Spring boot recupera el contexto para el usuario y recien ahi llama al controlador
    }*/

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public ResponseEntity<?> crearUsuario(UsuarioInputDTO inputDTO){
        return webApiCallerService.postEntity(this.usuarioServiceUrl + "/crear", inputDTO, Void.class);
    }

    public ResponseEntity<?> cambiarContrasenia(CambiarContraseniaDtoInput dto) {
        return webApiCallerService.postEntity(this.usuarioServiceUrl + "/editar/campos-escalares", dto, Void.class);
    }

    public ResponseEntity<?> editarUsuario(EditarUsuarioDtoInput dto) {
        return webApiCallerService.postEntity(this.usuarioServiceUrl + "/editar/campos-escalares", dto, Void.class);
    }

    public ResponseEntity<?> editarNombreDeUsuario(EditarNombreDeUsuarioDtoInput dto) {
        return webApiCallerService.postEntity(this.usuarioServiceUrl + "/editar/nombre-usuario", dto, Void.class);
    }

    public ResponseEntity<?> getAll(Long id) {
        return webApiCallerService.getEntity(this.usuarioServiceUrl + "/get-all?id=" + id, Void.class);
    }

    public ResponseEntity<?> getUsuario() {
        return webApiCallerService.getEntity(this.usuarioServiceUrl + "/get/usuario", Void.class);
    }

    public ResponseEntity<?> obtenerMensajes() {
        return webApiCallerService.getEntity(this.usuarioServiceUrl + "/get-mensajes", Void.class);
    }

}
