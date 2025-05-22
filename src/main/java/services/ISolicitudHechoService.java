package services;

import models.entities.Hecho;
import models.entities.SolicitudHecho;
import models.entities.personas.Usuario;

public interface ISolicitudHechoService {

    public Integer solicitarSubirHecho(Hecho hecho, Usuario usuario);
    public Integer evaluarSolicitudSubirHecho(Usuario usuario, SolicitudHecho solicitud, Boolean respuesta);
    public Integer evaluarEliminacionHecho(Usuario usuario, SolicitudHecho solicitud, Boolean respuesta);
    public Integer solicitarEliminacionHecho(Usuario Usuario, Hecho hecho);
}
