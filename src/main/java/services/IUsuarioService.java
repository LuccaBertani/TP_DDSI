package services;

import models.entities.personas.DatosPersonalesPublicador;

public interface IUsuarioService {
    public void crearUsuario(String contrasenia, DatosPersonalesPublicador datosPersonales);
}
