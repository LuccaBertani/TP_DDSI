package services;

import models.dtos.input.SolicitudHechoEliminarInputDTO;
import models.dtos.input.SolicitudHechoEvaluarInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.entities.Hecho;
import models.entities.RespuestaHttp;
import models.entities.SolicitudHecho;
import models.entities.personas.Usuario;

public interface ISolicitudHechoService {
    public RespuestaHttp<Integer> solicitarSubirHecho(SolicitudHechoInputDTO dto);
    public RespuestaHttp<Integer> evaluarSolicitudSubirHecho(SolicitudHechoEvaluarInputDTO dto);
    public RespuestaHttp<Integer> evaluarEliminacionHecho(SolicitudHechoEvaluarInputDTO dto);
    public RespuestaHttp<Integer> solicitarEliminacionHecho(SolicitudHechoEliminarInputDTO dto);
}
