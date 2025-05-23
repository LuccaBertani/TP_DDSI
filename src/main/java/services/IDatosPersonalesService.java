package services;

import models.entities.RespuestaHttp;
import models.entities.personas.Usuario;

import java.util.List;

public interface IDatosPersonalesService {
    public RespuestaHttp<List<Usuario>> obtenerListaContribuyentes(Long id_usuario);
}
