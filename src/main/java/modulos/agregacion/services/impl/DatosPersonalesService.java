package modulos.agregacion.services.impl;

import modulos.shared.RespuestaHttp;
import modulos.usuario.Rol;
import modulos.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import modulos.agregacion.repositories.IUsuarioRepository;

import java.util.List;

@Service
public class DatosPersonalesService {

    private final IUsuarioRepository personasRepo;

    public DatosPersonalesService(IUsuarioRepository personasRepo) {
        this.personasRepo = personasRepo;
    }

    public RespuestaHttp<List<Usuario>> obtenerListaContribuyentes(Long id_usuario) {
        Usuario usuario = personasRepo.findById(id_usuario);
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)) {
            List<Usuario> usuarios = personasRepo.findAll();
            return new RespuestaHttp<>(usuarios, HttpStatus.OK.value());
        }
        return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
    }

}
