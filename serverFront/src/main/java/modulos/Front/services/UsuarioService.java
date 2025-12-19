package modulos.Front.services;

import modulos.Front.dtos.input.*;
import modulos.Front.dtos.output.MensajeOutputDTO;
import modulos.Front.dtos.output.UsuarioOutputDto;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UsuarioService {

    private final WebApiCallerService webApiCallerService;
    private String usuarioServiceUrl = "/api/usuario";

    public UsuarioService(WebApiCallerService webApiCallerService) {
        this.webApiCallerService = webApiCallerService;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public String getUsernameFromSession(){
        return webApiCallerService.getUsernameFromSession();
    }

    public ResponseEntity<?> crearUsuario(UsuarioInputDTO inputDTO){
        return webApiCallerService.postEntityTokenOpcional(this.usuarioServiceUrl + "/public/crear", inputDTO, Void.class);
    }

    public ResponseEntity<?> cambiarContrasenia(CambiarContraseniaDtoInput dto) {
        return webApiCallerService.postEntity(this.usuarioServiceUrl + "/editar/contrasenia", dto, Void.class);
    }

    public ResponseEntity<?> editarUsuario(EditarUsuarioDtoInput dto) {
        return webApiCallerService.postEntity(this.usuarioServiceUrl + "/editar/campos-escalares", dto, Void.class);
    }

    public ResponseEntity<?> editarNombreDeUsuario(EditarNombreDeUsuarioDtoInput dto) {
        return webApiCallerService.postEntity(this.usuarioServiceUrl + "/editar/nombre-usuario", dto, Void.class);
    }

    public ResponseEntity<?> getAll() {
        return webApiCallerService.getEntity(this.usuarioServiceUrl + "/get-all", Void.class);
    }

    public ResponseEntity<UsuarioOutputDto> getUsuario() {
        return webApiCallerService.getEntity(this.usuarioServiceUrl + "/get/usuario", UsuarioOutputDto.class);
    }

    public ResponseEntity<?> obtenerMensajes() {
        return webApiCallerService.getList(this.usuarioServiceUrl + "/get-mensajes", MensajeOutputDTO.class);
    }

}
