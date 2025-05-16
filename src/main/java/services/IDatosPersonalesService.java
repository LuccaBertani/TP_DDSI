package services;

import models.entities.personas.Usuario;

import java.util.List;

public interface IDatosPersonalesService {
    public List<Usuario> obtenerListaContribuyentes(Usuario usuario);
}
