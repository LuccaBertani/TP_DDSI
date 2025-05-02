package services;

import models.entities.Hecho;
import models.entities.SolicitudHecho;
import models.entities.personas.Persona;
import models.repositories.ISolicitudHechoRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface ISolicitudHechoService {

    public void solicitarSubirHecho(Hecho hecho, Persona persona);
    public void evaluarSolicitudSubirHecho(SolicitudHecho solicitud, Boolean respuesta);
    public void evaluarEliminacionHecho(SolicitudHecho solicitud, Boolean respuesta);
}
