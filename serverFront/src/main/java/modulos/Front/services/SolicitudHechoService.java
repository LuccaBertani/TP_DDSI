package modulos.Front.services;

import jakarta.validation.Valid;
import modulos.Front.dtos.input.*;
import modulos.Front.dtos.output.ColeccionOutputDTO;
import modulos.Front.dtos.output.SolicitudHechoOutputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class SolicitudHechoService {
    private final WebApiCallerService webApiCallerService;
    private String solicitudHechoServiceUrl = "/api/solicitud-hecho";

    public SolicitudHechoService(WebApiCallerService webApiCallerService) {
        this.webApiCallerService = webApiCallerService;
    }

    public ResponseEntity<?> evaluarSolicitudSubida(SolicitudHechoEvaluarInputDTO dtoInput) {
        return webApiCallerService.postEntity(solicitudHechoServiceUrl + "/evaluar/subir", dtoInput, ColeccionOutputDTO.class);
    }

    public ResponseEntity<?> evaluarSolicitudEliminacion(SolicitudHechoEvaluarInputDTO dto) {
        return webApiCallerService.postEntity(this.solicitudHechoServiceUrl + "/evaluar/eliminar", dto, Void.class);
    }

    public ResponseEntity<?> getAllReportes(Long id_usuario){
        return webApiCallerService.getList(this.solicitudHechoServiceUrl + "/reportes/get/all" + id_usuario, SolicitudHechoOutputDTO.class);
    }

    public ResponseEntity<?> getAllSolicitudes(Long id_usuario){
        return webApiCallerService.getList(this.solicitudHechoServiceUrl + "/get/all" + id_usuario, SolicitudHechoOutputDTO.class);
    }

    public ResponseEntity<?> evaluarReporte(EvaluarReporteInputDTO dtoInput){
        return webApiCallerService.postEntity(this.solicitudHechoServiceUrl + "/reportes/evaluar", dtoInput, Void.class);
    }

    public ResponseEntity<?> getSolicitudesPendientes(Long id_usuario){
        return webApiCallerService.getList(this.solicitudHechoServiceUrl + "/get/pendientes?id_usuario=" + id_usuario, SolicitudHechoOutputDTO.class);
    }

    public ResponseEntity<?> evaluarSolicitudModificacion(SolicitudHechoModificarInputDTO dto) {
        return webApiCallerService.postEntity(this.solicitudHechoServiceUrl + "/evaluar/modificar", dto, Void.class);
    }

    public ResponseEntity<?> enviarSolicitudEliminarHecho(@Valid SolicitudHechoEliminarInputDTO dto) {
        return webApiCallerService.postEntity(this.solicitudHechoServiceUrl + "/eliminar-hecho", dto, Void.class);
    }

    public ResponseEntity<?> enviarSolicitudModificarHecho(@Valid SolicitudHechoEliminarInputDTO dto) {
        return webApiCallerService.postEntity(this.solicitudHechoServiceUrl + "/modificar-hecho", dto, Void.class);
    }

    public ResponseEntity<?> enviarSolicitudSubirHecho(@Valid SolicitudHechoInputDTO dto) {
        return webApiCallerService.postEntity(this.solicitudHechoServiceUrl + "/subir-hecho", dto, Void.class);
    }

    public ResponseEntity<?> reportarHecho(@Valid String motivo, @Valid Long idHecho, @Valid String fuente) {
        return webApiCallerService.postEntity(this.solicitudHechoServiceUrl + "/reportes/reportar?id_hecho=" + idHecho + "&fuente=" + fuente + "&motivo=" + motivo, Void.class);
    }

    public ResponseEntity<?> obtenerMensajes(@Valid Long idReceptor) {
        return webApiCallerService.getEntity(this.solicitudHechoServiceUrl + "/get-mensajes?id_receptor=" + idReceptor, Void.class);
    }
}
