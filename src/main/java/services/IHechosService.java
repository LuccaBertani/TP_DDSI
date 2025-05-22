package services;
import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.fuentes.Fuente;
import models.entities.personas.Usuario;

import java.util.List;

public interface IHechosService {
    public RespuestaHttp<Integer> subirHecho(Hecho hecho, Usuario usuario); // Habria que ver si la persona es administradora
    public RespuestaHttp<Integer> importarHechos(Fuente fuente, Usuario usuario);
    public void navegarPorHechos(List<Filtro> filtros, Coleccion coleccion);
    public void navegarPorHechos(Coleccion coleccion);
}
