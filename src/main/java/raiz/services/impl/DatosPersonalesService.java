package raiz.services.impl;

import raiz.models.entities.RespuestaHttp;
import raiz.models.entities.personas.Rol;
import raiz.models.entities.personas.Usuario;
import raiz.models.repositories.IPersonaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import raiz.services.IDatosPersonalesService;

import java.util.List;

@Service
public class DatosPersonalesService implements IDatosPersonalesService {

    private final IPersonaRepository personasRepo;

    public DatosPersonalesService(IPersonaRepository personasRepo) {
        this.personasRepo = personasRepo;
    }

    @Override
    public RespuestaHttp<List<Usuario>> obtenerListaContribuyentes(Long id_usuario) {
        Usuario usuario = personasRepo.findById(id_usuario);
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)) {
            List<Usuario> usuarios = personasRepo.findAll();
            return new RespuestaHttp<>(usuarios, HttpStatus.OK.value());
        }
        return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
    }

}
