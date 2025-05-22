package services;

import models.entities.personas.DatosPersonalesPublicador;

public interface IUsuarioService {
    public Integer crearUsuario(String contrasenia, DatosPersonalesPublicador datosPersonales);
}
