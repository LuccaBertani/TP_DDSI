package raiz.services;

import jakarta.validation.Valid;
import raiz.models.dtos.input.SolicitudHechoEliminarInputDTO;
import raiz.models.dtos.input.SolicitudHechoEvaluarInputDTO;
import raiz.models.dtos.input.SolicitudHechoInputDTO;
import raiz.models.dtos.input.SolicitudHechoModificarInputDTO;
import raiz.models.dtos.output.MensajesHechosUsuarioOutputDTO;
import raiz.models.entities.RespuestaHttp;
import raiz.models.entities.SolicitudHecho;

import java.util.List;

public interface ISolicitudHechoService {
    public RespuestaHttp<Void> solicitarSubirHecho(SolicitudHechoInputDTO dto);
    public RespuestaHttp<Void> evaluarSolicitudSubirHecho(SolicitudHechoEvaluarInputDTO dto);
    public RespuestaHttp<Void> evaluarEliminacionHecho(SolicitudHechoEvaluarInputDTO dto);
    public RespuestaHttp<Void> solicitarEliminacionHecho(SolicitudHechoEliminarInputDTO dto);
    public RespuestaHttp<Void> solicitarModificacionHecho(SolicitudHechoModificarInputDTO dto);
    public RespuestaHttp<Void> evaluarModificacionHecho(SolicitudHechoEvaluarInputDTO dtoInput);
    RespuestaHttp<List<MensajesHechosUsuarioOutputDTO>> enviarMensajes(Long idUsuario);
    public List<SolicitudHecho> obtenerSolicitudesPendientes();

    RespuestaHttp<Void> reportarHecho(@Valid String motivo, @Valid Long id_hecho);
}
