package services;
import models.entities.*;
import models.entities.fuentes.Fuente;
import models.entities.personas.Persona;

public interface IHechosService {
    public void subirHecho(Hecho hecho, Persona persona); // Habria que ver si la persona es administradora
    public void importarHechos(Fuente fuente, Persona persona);
}
