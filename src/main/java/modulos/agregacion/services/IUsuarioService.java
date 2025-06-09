package modulos.agregacion.services;

import modulos.shared.dtos.input.UsuarioInputDTO;
import modulos.shared.RespuestaHttp;
import modulos.usuario.Usuario;

public interface IUsuarioService {
    public RespuestaHttp<Usuario> crearUsuario(UsuarioInputDTO inputDTO);
}
