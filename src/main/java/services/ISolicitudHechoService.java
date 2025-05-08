package services;

import models.entities.Hecho;
import models.entities.SolicitudHecho;
import models.entities.personas.Persona;
import models.entities.personas.Usuario;

public interface ISolicitudHechoService {

    public void solicitarSubirHecho(Hecho hecho, Usuario usuario);
    public void evaluarSolicitudSubirHecho(SolicitudHecho solicitud, Boolean respuesta);
    public void evaluarEliminacionHecho(SolicitudHecho solicitud, Boolean respuesta);
    public void solicitarEliminacionHecho(Usuario Usuario, Hecho hecho);
}
