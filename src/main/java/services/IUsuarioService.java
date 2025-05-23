package services;

import models.dtos.input.UsuarioInputDTO;
import models.entities.RespuestaHttp;
import models.entities.personas.DatosPersonalesPublicador;
import models.entities.personas.Usuario;

public interface IUsuarioService {
    public RespuestaHttp<Usuario> crearUsuario(UsuarioInputDTO inputDTO);
}
