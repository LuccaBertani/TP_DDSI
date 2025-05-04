package services;

import models.entities.DatosPersonalesPublicador;
import models.entities.personas.Persona;

import java.util.List;

public interface IDatosPersonalesService {
    public List<Persona> obtenerListaContribuyentes(Persona persona);
}
