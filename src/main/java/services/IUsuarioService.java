package services;

import models.entities.DatosPersonalesPublicador;

public interface IUsuarioService {
    public void crearUsuario(String contrasenia, DatosPersonalesPublicador datosPersonales);
}
