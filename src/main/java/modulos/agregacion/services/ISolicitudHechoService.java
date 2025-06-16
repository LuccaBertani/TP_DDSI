package modulos.agregacion.services;

import jakarta.validation.Valid;
import modulos.shared.dtos.input.SolicitudHechoEliminarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoEvaluarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.input.SolicitudHechoModificarInputDTO;
import modulos.shared.dtos.output.MensajesHechosUsuarioOutputDTO;
import modulos.shared.RespuestaHttp;
import modulos.solicitudes.SolicitudHecho;

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
