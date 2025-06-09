package modulos.agregacion.services;

import modulos.shared.RespuestaHttp;
import modulos.usuario.Usuario;

import java.util.List;

public interface IDatosPersonalesService {
    public RespuestaHttp<List<Usuario>> obtenerListaContribuyentes(Long id_usuario);
}
