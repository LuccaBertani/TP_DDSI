package modulos.agregacion.services;

import modulos.agregacion.repositories.IUsuarioRepository;
import modulos.shared.RespuestaHttp;
import modulos.usuario.Rol;
import modulos.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatosPersonalesService {

    private final IUsuarioRepository usuariosRepo;

    public DatosPersonalesService(IUsuarioRepository usuariosRepo) {
        this.usuariosRepo = usuariosRepo;
    }

    public RespuestaHttp<List<Usuario>> obtenerListaContribuyentes(Long id_usuario) {
        Usuario usuario = usuariosRepo.findById(id_usuario).orElse(null);
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)) {
            List<Usuario> usuarios = usuariosRepo.findAll();
            return new RespuestaHttp<>(usuarios, HttpStatus.OK.value());
        }
        return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
    }

}
