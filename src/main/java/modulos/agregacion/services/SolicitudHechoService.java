package modulos.agregacion.services;

import modulos.agregacion.entities.*;
import modulos.agregacion.entities.solicitudes.*;
import modulos.agregacion.repositories.*;
import modulos.buscadores.BuscadorProvincia;
import modulos.agregacion.entities.fuentes.Origen;
import modulos.shared.dtos.input.SolicitudHechoEliminarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoEvaluarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.input.SolicitudHechoModificarInputDTO;
import modulos.shared.dtos.output.MensajesHechosUsuarioOutputDTO;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorPais;
import modulos.shared.utils.DetectorDeSpam;
import modulos.shared.utils.FechaParser;
import modulos.agregacion.entities.fuentes.FuenteDinamica;
import modulos.agregacion.entities.RespuestaHttp;
import modulos.shared.utils.GestorRoles;
import modulos.agregacion.entities.usuario.Rol;
import modulos.agregacion.entities.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolicitudHechoService {

    //private final IDetectorDeSpam detectorDeSpam;
    private final ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo;
    private final ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo;
    private final ISolicitudModificarHechoRepository solicitudModificarHechoRepo;
    private final IHechosProxyRepository hechosProxyRepository;
    private final IHechosEstaticaRepository hechosEstaticaRepository;
    private final IHechosDinamicaRepository hechosDinamicaRepository;
    private final IUsuarioRepository usuariosRepository;
    private final IMensajeRepository mensajesRepository;
    private final IReporteHechoRepository reportesHechoRepository;

    GestorRoles gestorRoles;

    public SolicitudHechoService(ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo,
                                 ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo,
                                 ISolicitudModificarHechoRepository solicitudModificarHechoRepo,
                                 IUsuarioRepository usuariosRepository,
                                 IHechosProxyRepository hechosProxyRepository,
                                 IHechosEstaticaRepository hechosEstaticaRepository,
                                 IHechosDinamicaRepository hechosDinamicaRepository,
                                 IMensajeRepository mensajesRepository,
                                 IReporteHechoRepository reportesHechoRepository) {
        this.solicitudAgregarHechoRepo = solicitudAgregarHechoRepo;
        this.solicitudEliminarHechoRepo = solicitudEliminarHechoRepo;
        this.hechosProxyRepository = hechosProxyRepository;
        this.hechosDinamicaRepository = hechosDinamicaRepository;
        this.hechosEstaticaRepository = hechosEstaticaRepository;
        this.usuariosRepository = usuariosRepository;
        this.solicitudModificarHechoRepo = solicitudModificarHechoRepo;
        this.mensajesRepository = mensajesRepository;
        this.reportesHechoRepository = reportesHechoRepository;
        gestorRoles = new GestorRoles();
    }

    public RespuestaHttp<Void> solicitarSubirHecho(SolicitudHechoInputDTO dto) {

        Usuario usuario = usuariosRepository.findById(dto.getId_usuario()).orElse(null);

        if (usuario!=null && usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        Pais pais = BuscadorPais.buscarOCrear(hechosDinamicaRepository.findAll(),dto.getPais(),hechosProxyRepository.findAll(),hechosEstaticaRepository.findAll());
        Provincia provincia = BuscadorProvincia.buscarOCrear(hechosDinamicaRepository.findAll(),dto.getProvincia(),hechosProxyRepository.findAll(),hechosEstaticaRepository.findAll());
        HechosData hechosData = new HechosData(dto.getTitulo(), dto.getDescripcion(), dto.getTipoContenido(),
                pais, dto.getFechaAcontecimiento(), provincia);

        FuenteDinamica fuenteDinamica = new FuenteDinamica();
        HechoDinamica hecho = fuenteDinamica.crearHecho(hechosData);
        SolicitudSubirHecho solicitudHecho = new SolicitudSubirHecho(usuario, hecho);

        if (DetectorDeSpam.esSpam(dto.getTitulo()) || DetectorDeSpam.esSpam(dto.getDescripcion())) {
            solicitudHecho.setProcesada(true);
            solicitudHecho.setRechazadaPorSpam(true);
            solicitudAgregarHechoRepo.save(solicitudHecho);
            return new RespuestaHttp<>(null, HttpStatus.BAD_REQUEST.value());
        }
        solicitudAgregarHechoRepo.save(solicitudHecho);
        if (usuario!=null)
            hecho.setUsuario(usuario);

        hechosDinamicaRepository.save(hecho);

        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    //El usuario manda una solicitud para eliminar un hecho -> guardar la solicitud en la base de datos
    // Asumimos que la solicitud de eliminación puede venir de una persona que no haya subido el hecho solicitado
    public RespuestaHttp<Void> solicitarEliminacionHecho(SolicitudHechoEliminarInputDTO dto){
        Usuario usuario = usuariosRepository.findById(dto.getId_usuario()).orElse(null);
        if (usuario == null || usuario.getRol().equals(Rol.ADMINISTRADOR) || usuario.getRol().equals(Rol.VISUALIZADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        HechoDinamica hecho = hechosDinamicaRepository.findById(dto.getId_hecho()).orElse(null);

        if (hecho == null){
            return new RespuestaHttp<>(null, HttpStatus.NO_CONTENT.value());
        }

        SolicitudEliminarHecho solicitud = new SolicitudEliminarHecho(usuario, hecho, dto.getJustificacion());

        if (DetectorDeSpam.esSpam(dto.getJustificacion())) {
            // Marcar como rechazada por spam y guardar
            solicitud.setProcesada(true);
            solicitud.setRechazadaPorSpam(true);
            solicitudEliminarHechoRepo.save(solicitud);
            return new RespuestaHttp<>(null, HttpStatus.BAD_REQUEST.value()); // 400 - solicitud rechazada por spam
        }
        solicitudEliminarHechoRepo.save(solicitud);
        return new RespuestaHttp<>(null, HttpStatus.OK.value()); // Un admin no debería solicitar eliminar, los elimina directamente
    }

    public RespuestaHttp<Void> solicitarModificacionHecho(SolicitudHechoModificarInputDTO dto){

        Usuario usuario = usuariosRepository.findById(dto.getId_usuario()).orElse(null);

        HechoDinamica hecho = hechosDinamicaRepository.findById(dto.getId_hecho()).orElse(null);

        if (usuario == null || hecho == null || usuario.getId().equals(hecho.getUsuario().getId()) || usuario.getRol().equals(Rol.ADMINISTRADOR) || usuario.getRol().equals(Rol.VISUALIZADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        SolicitudModificarHecho solicitud = new SolicitudModificarHecho(usuario, hecho);

        if (DetectorDeSpam.esSpam(dto.getTitulo()) || DetectorDeSpam.esSpam(dto.getDescripcion()))
        {
            solicitud.setProcesada(true);
            solicitud.setRechazadaPorSpam(true);
            solicitudModificarHechoRepo.save(solicitud);
            return new RespuestaHttp<>(null, HttpStatus.BAD_REQUEST.value());
        }

        if (ChronoUnit.DAYS.between(hecho.getAtributosHecho().getFechaCarga(), ZonedDateTime.now()) >= 7){
            return new RespuestaHttp<>(null, HttpStatus.CONFLICT.value()); // Error 409: cuando la solicitud es válida, pero no puede procesarse por estado actual del recurso
        }

        hecho.getAtributosHecho().setTitulo(dto.getTitulo());
        hecho.getAtributosHecho().getUbicacion().setPais(BuscadorPais.buscarOCrear(hechosDinamicaRepository.findAll(), dto.getPais(), hechosProxyRepository.findAll(), hechosEstaticaRepository.findAll()));
        hecho.getAtributosHecho().getUbicacion().setProvincia(BuscadorProvincia.buscarOCrear(hechosDinamicaRepository.findAll(), dto.getProvincia(), hechosProxyRepository.findAll(), hechosEstaticaRepository.findAll()));
        hecho.getAtributosHecho().setCategoria(BuscadorCategoria.buscarOCrear(hechosDinamicaRepository.findAll(), dto.getPais(), hechosProxyRepository.findAll(), hechosEstaticaRepository.findAll()));
        hecho.getAtributosHecho().setFechaAcontecimiento(FechaParser.parsearFecha(dto.getFechaAcontecimiento()));
        hecho.getAtributosHecho().setContenidoMultimedia(TipoContenido.fromCodigo(dto.getTipoContenido()));
        solicitudModificarHechoRepo.save(solicitud);
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }


    public RespuestaHttp<Void> evaluarSolicitudSubirHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudAgregarHechoRepo.findById(dtoInput.getId_solicitud()).orElse(null);
        if (solicitud == null) {
            return new RespuestaHttp<>(null, HttpStatus.NOT_FOUND.value());
        }

        // Verificar que no haya sido procesada ya
        if (solicitud.isProcesada()) {
            return new RespuestaHttp<>(null, HttpStatus.CONFLICT.value()); // Ya fue procesada
        }

        Usuario usuario = usuariosRepository.findById(dtoInput.getId_usuario()).orElse(null);//el que ejecuta la acción

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        else {
            // Marcar como procesada
            solicitud.setProcesada(true);
            if (dtoInput.getRespuesta()) {
                solicitud.getHecho().setActivo(true);
                solicitud.getHecho().getAtributosHecho().setFechaCarga(ZonedDateTime.now());
                solicitud.getHecho().getAtributosHecho().setFechaUltimaActualizacion(solicitud.getHecho().getAtributosHecho().getFechaCarga()); // Nueva fecha de modificación
                solicitud.getUsuario().incrementarHechosSubidos();

                hechosDinamicaRepository.save(solicitud.getHecho());

                if (solicitud.getUsuario().getRol().equals(Rol.VISUALIZADOR)){
                    gestorRoles.VisualizadorAContribuyente(solicitud.getUsuario());
                }
            }
            this.solicitudAgregarHechoRepo.delete(solicitud);
        }
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    public RespuestaHttp<Void> evaluarEliminacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudEliminarHechoRepo.findById(dtoInput.getId_solicitud()).orElse(null);

        Usuario usuario = usuariosRepository.findById(dtoInput.getId_usuario()).orElse(null);//el que ejecuta la acción

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        solicitud.setProcesada(true);
        if (dtoInput.getRespuesta()) {
            solicitud.getUsuario().disminuirHechosSubidos();
            hechosDinamicaRepository.delete(solicitud.getHecho());

            if (solicitud.getUsuario().getCantHechosSubidos() == 0){
                gestorRoles.ContribuyenteAVisualizador(solicitud.getUsuario());
            }
        }


        solicitudEliminarHechoRepo.delete(solicitud);
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    public RespuestaHttp<Void> evaluarModificacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudModificarHechoRepo.findById(dtoInput.getId_solicitud()).orElse(null);
        Usuario usuario = usuariosRepository.findById(dtoInput.getId_usuario()).orElse(null);//el que ejecuta la acción

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        else {
            solicitud.setProcesada(true);
            if (dtoInput.getRespuesta()) {
                // El hecho debe modificarse
                solicitud.getHecho().getAtributosHecho().setFechaUltimaActualizacion(ZonedDateTime.now());

                solicitud.getHecho().getAtributosHecho().setModificado(true);
                hechosDinamicaRepository.save(solicitud.getHecho());
            }
        }
        solicitudModificarHechoRepo.delete(solicitud);
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    public RespuestaHttp<List<MensajesHechosUsuarioOutputDTO>> enviarMensajes(Long id_Usuario){
        List<Mensaje> mensajesTotales = this.mensajesRepository.findAll();
        List<Mensaje> mensajesUsuario = Filtrador.filtrarMensajes(mensajesTotales,id_Usuario);
        List<MensajesHechosUsuarioOutputDTO> outputDTO = new ArrayList<>();

        for(Mensaje mensaje : mensajesUsuario){

            MensajesHechosUsuarioOutputDTO output = new MensajesHechosUsuarioOutputDTO();

            output.setId_hecho(mensaje.getSolicitud_hecho().getHecho().getId());
            output.setId_mensaje(mensaje.getId());
            output.setId_usuario(mensaje.getReceptor().getId());
            output.setMensaje(mensaje.getTextoMensaje());

            outputDTO.add(output);
        }

        return new RespuestaHttp<>(outputDTO, HttpStatus.OK.value());

    }

    public List<SolicitudHecho> obtenerSolicitudesPendientes() {
        return solicitudEliminarHechoRepo.findAll().stream()
                .filter(s -> !s.isProcesada() && !s.isRechazadaPorSpam())
                .toList();
    }

    public RespuestaHttp<Void> reportarHecho(String motivo, Long id_hecho) {
        Hecho hecho = hechosDinamicaRepository.findById(id_hecho).orElse(null);
        if(hecho == null){
            hecho = hechosEstaticaRepository.findById(id_hecho).orElse(null);
            if(hecho == null){
                hecho = hechosProxyRepository.findById(id_hecho).orElse(null);
                if(hecho == null){
                    return new RespuestaHttp<>(null, HttpStatus.NO_CONTENT.value());
                }
            }
        }
        Reporte reporte = new Reporte(motivo, hecho);
        reportesHechoRepository.save(reporte);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

}


