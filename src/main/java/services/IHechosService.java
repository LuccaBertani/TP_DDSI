package services;
import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.fuentes.Fuente;
import models.entities.personas.Persona;

import java.util.List;

public interface IHechosService {
    public void subirHecho(Hecho hecho, Persona persona); // Habria que ver si la persona es administradora
    public void importarHechos(Fuente fuente, Persona persona);
    public void navegarPorHechos(List<Filtro> filtros, Coleccion coleccion);
    public void navegarPorHechos(Coleccion coleccion);
}
