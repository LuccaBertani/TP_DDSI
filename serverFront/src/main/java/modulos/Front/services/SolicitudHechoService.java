package modulos.Front.services;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import modulos.Front.dtos.input.*;
import modulos.Front.dtos.output.ColeccionOutputDTO;
import modulos.Front.dtos.output.RolCambiadoDTO;
import modulos.Front.dtos.output.SolicitudHechoOutputDTO;
import modulos.Front.sessionHandlers.ActiveSessionTracker;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SolicitudHechoService {
    private final WebApiCallerService webApiCallerService;
    private final ActiveSessionTracker activeSessionTracker;
    private String solicitudHechoServiceUrl = "/api/solicitud-hecho";

    public SolicitudHechoService(WebApiCallerService webApiCallerService, ActiveSessionTracker activeSessionTracker) {
        this.webApiCallerService = webApiCallerService;
        this.activeSessionTracker = activeSessionTracker;
    }

    public ResponseEntity<?> evaluarSolicitudSubida(SolicitudHechoEvaluarInputDTO dtoInput) {
        ResponseEntity<?> rta = webApiCallerService.postEntity(solicitudHechoServiceUrl + "/evaluar/subir", dtoInput, RolCambiadoDTO.class);
        this.actualizarSesiones(rta);
        return rta;
    }

    public ResponseEntity<?> evaluarSolicitudEliminacion(SolicitudHechoEvaluarInputDTO dto) {
        ResponseEntity<?> rta = webApiCallerService.postEntity(this.solicitudHechoServiceUrl + "/evaluar/eliminar", dto, RolCambiadoDTO.class);
        this.actualizarSesiones(rta);
        return rta;
    }

    public ResponseEntity<?> getAllReportes(Long id_usuario){
        return webApiCallerService.getList(this.solicitudHechoServiceUrl + "/reportes/get/all" + id_usuario, SolicitudHechoOutputDTO.class);
    }

    public ResponseEntity<?> getAllSolicitudes(Long id_usuario){
        return webApiCallerService.getList(this.solicitudHechoServiceUrl + "/get/all" + id_usuario, SolicitudHechoOutputDTO.class);
    }

    public ResponseEntity<?> evaluarReporte(EvaluarReporteInputDTO dtoInput){
        ResponseEntity<?> rta = webApiCallerService.postEntity(this.solicitudHechoServiceUrl + "/reportes/evaluar", dtoInput, RolCambiadoDTO.class);
        this.actualizarSesiones(rta);
        return rta;
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

    private void actualizarSesiones(ResponseEntity<?> rta) {
        if (rta.getStatusCode().is2xxSuccessful()) {
            RolCambiadoDTO dtoOutput = (RolCambiadoDTO) rta.getBody();

            if (dtoOutput != null && dtoOutput.getRolModificado()) {
                List<HttpSession> sesionesActivas = this.activeSessionTracker
                        .sesionesAsociadasAUsuario(dtoOutput.getUsername());

                sesionesActivas.forEach(sesion -> {
                    SecurityContext context = (SecurityContext) sesion.getAttribute("SPRING_SECURITY_CONTEXT");

                    if (context != null) {
                        Authentication oldAuth = context.getAuthentication();

                        if (oldAuth != null) {
                            // Copiamos las authorities actuales en una lista mutable
                            List<GrantedAuthority> nuevasAuthorities = new ArrayList<>(oldAuth.getAuthorities());

                            // Eliminamos los roles anteriores (ROLE_)
                            nuevasAuthorities.removeIf(a -> a.getAuthority().startsWith("ROLE_"));

                            // Agregamos el nuevo rol
                            nuevasAuthorities.add(new SimpleGrantedAuthority("ROLE_" + dtoOutput.getRol().name()));

                            // Creamos una nueva Authentication con las nuevas authorities
                            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                                    oldAuth.getPrincipal(),
                                    oldAuth.getCredentials(),
                                    nuevasAuthorities
                            );

                            // Reemplazamos el Authentication en el SecurityContext
                            context.setAuthentication(newAuth);

                            // Persistimos el cambio en la sesión
                            sesion.setAttribute("SPRING_SECURITY_CONTEXT", context);
                        }
                    }
                });
            }
        }
    }

}
