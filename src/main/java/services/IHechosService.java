package services;
import models.entities.*;
import models.entities.personas.Persona;

public interface IHechosService {
    public void subirHecho(Hecho hecho, Persona persona); // Habria que ver si la persona es administradora
    public void solicitarSubirHecho(Hecho hecho, Persona persona);
    public void evaluarSolicitudSubirHecho(SolicitudHecho solicitud, Boolean respuesta);
    public void evaluarEliminacionHecho(SolicitudHecho solicitud, Boolean respuesta);
    public void importarHechos(Fuente fuente);
}
