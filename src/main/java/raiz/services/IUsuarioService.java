package raiz.services;

import raiz.models.dtos.input.UsuarioInputDTO;
import raiz.models.entities.RespuestaHttp;
import raiz.models.entities.personas.Usuario;

public interface IUsuarioService {
    public RespuestaHttp<Usuario> crearUsuario(UsuarioInputDTO inputDTO);
}
