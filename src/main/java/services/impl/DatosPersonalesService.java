package services.impl;

import models.dtos.input.ColeccionInputDTO;
import models.dtos.input.DatosPersonalesInputDTO;
import models.dtos.input.UsuarioInputDTO;
import models.entities.HttpCode;
import models.entities.RespuestaHttp;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import services.IDatosPersonalesService;

import java.util.List;

@Service
public class DatosPersonalesService implements IDatosPersonalesService {

    private final IPersonaRepository personasRepo;

    public DatosPersonalesService(IPersonaRepository personasRepo) {
        this.personasRepo = personasRepo;
    }

    @Override
    public RespuestaHttp<List<Usuario>> obtenerListaContribuyentes(DatosPersonalesInputDTO inputDTO) {
        Usuario usuario = personasRepo.findById(inputDTO.getId_usuario());
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)) {
            List<Usuario> usuarios = personasRepo.findAll();
            return new RespuestaHttp<>(usuarios, HttpCode.OK.getCode());
        }
        return new RespuestaHttp<>(null, HttpCode.UNAUTHORIZED.getCode());
    }

}
