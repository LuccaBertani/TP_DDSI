package modulos.agregacion.services.impl;

import modulos.shared.dtos.input.UsuarioInputDTO;
import modulos.shared.RespuestaHttp;
import modulos.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import modulos.agregacion.repositories.IUsuarioRepository;

@Service
public class  UsuarioService {

    private final IUsuarioRepository usuarioRepo;

    public UsuarioService(IUsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }
    //Momento en el que un usuario se registra y guarda datos personales (NO LLAMAR A ESTE METODO SI ES ANONIMO)
    public RespuestaHttp<Usuario> crearUsuario(UsuarioInputDTO inputDTO){

        Usuario usuario = new Usuario(usuarioRepo.getProxId());

        usuario.getDatosPersonales().setNombre(inputDTO.getNombre());
        usuario.getDatosPersonales().setApellido(inputDTO.getApellido());
        usuario.getDatosPersonales().setEdad(inputDTO.getEdad());
        usuario.setContrasenia(inputDTO.getContrasenia());
        usuarioRepo.save(usuario);
        return new RespuestaHttp<>(usuario, HttpStatus.OK.value());
    }

}
