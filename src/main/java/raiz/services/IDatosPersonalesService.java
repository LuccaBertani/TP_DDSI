package raiz.services;

import raiz.models.entities.RespuestaHttp;
import raiz.models.entities.personas.Usuario;

import java.util.List;

public interface IDatosPersonalesService {
    public RespuestaHttp<List<Usuario>> obtenerListaContribuyentes(Long id_usuario);
}
