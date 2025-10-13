package modulos.agregacion.services;

import modulos.agregacion.entities.DbMain.usuario.Rol;
import modulos.agregacion.repositories.DbMain.IUsuarioRepository;
import modulos.shared.dtos.input.*;
import modulos.agregacion.entities.DbMain.usuario.Usuario;
import modulos.shared.dtos.output.UsuarioOutputDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    private final IUsuarioRepository usuarioRepo;

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(IUsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    //Momento en el que un usuario se registra y guarda datos personales (NO LLAMAR A ESTE METODO SI ES ANONIMO)
    public ResponseEntity<?> crearUsuario(UsuarioInputDTO inputDTO){

        Usuario usuario = usuarioRepo.findByNombreDeUsuario(inputDTO.getNombreUsuario()).orElse(null);

        if(usuario != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya existe");
        }

        usuario = new Usuario();

        usuario.getDatosPersonales().setNombre(inputDTO.getNombre());
        usuario.setNombreDeUsuario(inputDTO.getNombreUsuario());
        usuario.getDatosPersonales().setApellido(inputDTO.getApellido());
        usuario.getDatosPersonales().setEdad(inputDTO.getEdad());
        usuario.setContrasenia(passwordEncoder.encode(inputDTO.getContrasenia()));
        usuarioRepo.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<?> iniciarSesion(LoginDtoInput login) {
        Usuario usuario = usuarioRepo.findByNombreDeUsuario(login.getNombreDeUsuario()).orElse(null);

        if (usuario == null || !passwordEncoder.matches(login.getContrasenia(), usuario.getContrasenia())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Nombre de usuario o contraseña incorrecto");
        }

        return ResponseEntity.ok(usuario.getId());
    }

    public ResponseEntity<?> cambiarContrasenia(CambiarContraseniaDtoInput dtoImput) {

        Usuario usuario = usuarioRepo.findById(dtoImput.getId_usuario()).orElse(null);

        if (usuario == null || !passwordEncoder.matches(dtoImput.getContrasenia_actual(), usuario.getContrasenia())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Nombre de usuario o contraseña incorrecto");
        }

        String contraseniaNuevaHash = passwordEncoder.encode(dtoImput.getContrasenia_nueva());
        usuario.setContrasenia(contraseniaNuevaHash);
        usuarioRepo.save(usuario);

        return ResponseEntity.ok().build();

    }

    public ResponseEntity<?> editarUsuario(EditarUsuarioDtoInput dtoImput) {

        Usuario usuario = usuarioRepo.findById(dtoImput.getId()).orElse(null);

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        Optional.ofNullable(dtoImput.getNombre()).ifPresent(usuario.getDatosPersonales()::setNombre);
        Optional.ofNullable(dtoImput.getApellido()).ifPresent(usuario.getDatosPersonales()::setApellido);
        Optional.ofNullable(dtoImput.getEdad()).ifPresent(usuario.getDatosPersonales()::setEdad);

        usuarioRepo.save(usuario);

        return ResponseEntity.ok().build();
    }


    public ResponseEntity<?> editarNombreDeUsuario(EditarNombreDeUsuarioDtoInput dtoImput) {

        Usuario usuario = usuarioRepo.findById(dtoImput.getId()).orElse(null);

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        if (!passwordEncoder.matches(dtoImput.getContrasenia(), usuario.getContrasenia())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Contraseña incorrecta"));
        }

        usuario.setNombreDeUsuario(dtoImput.getNombreDeUsuarioNuevo());

        usuarioRepo.save(usuario);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getAll(Long id) {

        Usuario usuario = usuarioRepo.findById(id).orElse(null);

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "El usuario no tiene permisos"));
        }

        List<UsuarioOutputDto> usuariosDto = new ArrayList<>();

        for(Usuario usuario123 : usuarioRepo.findAll()){
            UsuarioOutputDto usuarioDto = new UsuarioOutputDto();
            usuarioDto.setId(usuario123.getId());
            usuarioDto.setNombreDeUsuario(usuario123.getNombreDeUsuario());
            usuarioDto.setNombre(usuario123.getDatosPersonales().getNombre());
            usuarioDto.setApellido(usuario123.getDatosPersonales().getApellido());
            usuarioDto.setEdad(usuario123.getDatosPersonales().getEdad());
            usuarioDto.setCantHechosSubidos(usuario123.getCantHechosSubidos());
            usuariosDto.add(usuarioDto);
        }

        return ResponseEntity.ok(usuariosDto);
    }

    public ResponseEntity<?> getUsuarioByNombreUsuario(String nombre_usuario){
        Usuario usuario = usuarioRepo.findByNombreDeUsuario(nombre_usuario).orElse(null);
        if (usuario != null){
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }
}
