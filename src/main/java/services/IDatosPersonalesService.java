package services;

import models.dtos.input.ColeccionInputDTO;
import models.dtos.input.DatosPersonalesInputDTO;
import models.entities.RespuestaHttp;
import models.entities.personas.Usuario;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IDatosPersonalesService {
    public RespuestaHttp<List<Usuario>> obtenerListaContribuyentes(Long id_usuario);
}
