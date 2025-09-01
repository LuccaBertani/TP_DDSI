package modulos.agregacion.services;

import modulos.agregacion.repositories.IUsuarioRepository;
import modulos.shared.dtos.input.UsuarioInputDTO;
import modulos.agregacion.entities.RespuestaHttp;
import modulos.agregacion.entities.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final IUsuarioRepository usuarioRepo;

    public UsuarioService(IUsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }
    //Momento en el que un usuario se registra y guarda datos personales (NO LLAMAR A ESTE METODO SI ES ANONIMO)
    public RespuestaHttp<Usuario> crearUsuario(UsuarioInputDTO inputDTO){

        Usuario usuario = new Usuario();

        usuario.getDatosPersonales().setNombre(inputDTO.getNombre());
        usuario.getDatosPersonales().setApellido(inputDTO.getApellido());
        usuario.getDatosPersonales().setEdad(inputDTO.getEdad());
        usuario.setContrasenia(inputDTO.getContrasenia());
        usuarioRepo.save(usuario);
        return new RespuestaHttp<>(usuario, HttpStatus.OK.value());
    }

}
