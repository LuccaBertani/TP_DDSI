package modulos.agregacion.services;

import modulos.agregacion.entities.Filtrador;
import modulos.agregacion.repositories.*;
import modulos.fuentes.Origen;
import modulos.shared.*;
import modulos.shared.dtos.input.SolicitudHechoEliminarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoEvaluarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.input.SolicitudHechoModificarInputDTO;
import modulos.shared.dtos.output.MensajesHechosUsuarioOutputDTO;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorPais;
import modulos.shared.utils.DetectorDeSpam;
import modulos.shared.utils.FechaParser;
import modulos.fuentes.FuenteDinamica;
import modulos.solicitudes.Reporte;
import modulos.usuario.GestorRoles;
import modulos.usuario.Rol;
import modulos.usuario.Usuario;
import modulos.solicitudes.Mensaje;
import modulos.solicitudes.SolicitudHecho;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolicitudHechoService {

    //private final IDetectorDeSpam detectorDeSpam;
    private final IRepository<SolicitudHecho> solicitudAgregarHechoRepo;
    private final IRepository<SolicitudHecho> solicitudEliminarHechoRepo;
    private final IRepository<SolicitudHecho> solicitudModificarHechoRepo;
    private final IRepository<Hecho> hechosProxyRepository;
    private final IRepository<Hecho> hechosDinamicaRepository;
    private final IRepository<Hecho> hechosEstaticaRepository;
    private final IRepository<Usuario> usuariosRepository;
    private final IRepository<Mensaje> mensajesRepository;
    private final IRepository<Reporte> reportesHechoRepository;

    GestorRoles gestorRoles;

    public SolicitudHechoService(@Qualifier("SolicitudAgregarHechoRepository") IRepository<SolicitudHecho> solicitudAgregarHechoRepo,
                                 @Qualifier("SolicitudEliminarHechoRepository") IRepository<SolicitudHecho> solicitudEliminarHechoRepo,
                                 @Qualifier("SolicitudModificarHechoRepository") IRepository<SolicitudHecho> solicitudModificarHechoRepo,
                                 IRepository<Usuario> usuariosRepository,
                                 @Qualifier("hechosProxyRepo")IRepository<Hecho> hechosProxyRepository,
                                 @Qualifier("hechosDinamicaRepo") IRepository<Hecho> hechosDinamicaRepository,
                                 @Qualifier("hechosEstaticaRepo") IRepository<Hecho> hechosEstaticaRepository,
                                 IRepository<Mensaje> mensajesRepository,
                                 IRepository<Reporte> reportesHechoRepository) {
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

        Usuario usuario = usuariosRepository.findById(dto.getId_usuario());

        if (usuario!=null && usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        Pais pais = BuscadorPais.buscarOCrear(hechosProxyRepository.findAll(),dto.getPais(),hechosDinamicaRepository.findAll(),hechosEstaticaRepository.findAll());

        HechosData hechosData = new HechosData(dto.getTitulo(), dto.getDescripcion(), dto.getTipoContenido(),
                pais, dto.getFechaAcontecimiento(), hechosDinamicaRepository.getProxId());

        FuenteDinamica fuenteDinamica = new FuenteDinamica();
        Hecho hecho = fuenteDinamica.crearHecho(hechosData);
        SolicitudHecho solicitudHecho = new SolicitudHecho(usuario, hecho, solicitudAgregarHechoRepo.getProxId());
        if (DetectorDeSpam.esSpam(dto.getTitulo()) || DetectorDeSpam.esSpam(dto.getDescripcion())) {
            solicitudHecho.setProcesada(true);
            solicitudHecho.setRechazadaPorSpam(true);
            solicitudAgregarHechoRepo.save(solicitudHecho);
            return new RespuestaHttp<>(null, HttpStatus.BAD_REQUEST.value());
        }
        solicitudAgregarHechoRepo.save(solicitudHecho);
        if (usuario!=null)
            hecho.setId_usuario(usuario.getId());

        hechosDinamicaRepository.save(hecho);

        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    //El usuario manda una solicitud para eliminar un hecho -> guardar la solicitud en la base de datos
    // Asumimos que la solicitud de eliminación puede venir de una persona que no haya subido el hecho solicitado
    public RespuestaHttp<Void> solicitarEliminacionHecho(SolicitudHechoEliminarInputDTO dto){
        Usuario usuario = usuariosRepository.findById(dto.getId_usuario());
        if (usuario == null || usuario.getRol().equals(Rol.ADMINISTRADOR) || usuario.getRol().equals(Rol.VISUALIZADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        Hecho hecho;
        switch (Origen.fromCodigo(dto.getOrigen())){
            case FUENTE_DINAMICA -> hecho = hechosDinamicaRepository.findById(dto.getId_hecho());
            case FUENTE_ESTATICA -> hecho = hechosEstaticaRepository.findById(dto.getId_hecho());
            case FUENTE_PROXY_METAMAPA -> hecho = hechosProxyRepository.findById(dto.getId_hecho());
            default -> hecho = null;
        }

        SolicitudHecho solicitud = new SolicitudHecho(usuario, hecho, solicitudEliminarHechoRepo.getProxId(), dto.getJustificacion());
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

        Usuario usuario = usuariosRepository.findById(dto.getId_usuario());

        Hecho hecho;
        switch (Origen.fromCodigo(dto.getOrigen())){
            case FUENTE_DINAMICA -> hecho = hechosDinamicaRepository.findById(dto.getId_hecho());
            case FUENTE_ESTATICA -> hecho = hechosEstaticaRepository.findById(dto.getId_hecho());
            case FUENTE_PROXY_METAMAPA -> hecho = hechosProxyRepository.findById(dto.getId_hecho());
            default -> hecho = null;
        }

        if (usuario == null || hecho == null || usuario.getId().equals(hecho.getId_usuario()) || usuario.getRol().equals(Rol.ADMINISTRADOR) || usuario.getRol().equals(Rol.VISUALIZADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        SolicitudHecho solicitud = new SolicitudHecho(usuario, hecho, solicitudEliminarHechoRepo.getProxId());
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
        hecho.getAtributosHecho().setPais(BuscadorPais.buscarOCrear(hechosDinamicaRepository.findAll(), dto.getPais(), hechosProxyRepository.findAll(), hechosEstaticaRepository.findAll()));
        hecho.getAtributosHecho().setCategoria(BuscadorCategoria.buscarOCrear(hechosDinamicaRepository.findAll(), dto.getPais(), hechosProxyRepository.findAll(), hechosEstaticaRepository.findAll()));
        hecho.getAtributosHecho().setFechaAcontecimiento(FechaParser.parsearFecha(dto.getFechaAcontecimiento()));
        hecho.getAtributosHecho().setContenidoMultimedia(TipoContenido.fromCodigo(dto.getTipoContenido()));
        solicitudModificarHechoRepo.save(solicitud);
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }


    public RespuestaHttp<Void> evaluarSolicitudSubirHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudAgregarHechoRepo.findById(dtoInput.getId_solicitud());
        if (solicitud == null) {
            return new RespuestaHttp<>(null, HttpStatus.NOT_FOUND.value());
        }

        // Verificar que no haya sido procesada ya
        if (solicitud.isProcesada()) {
            return new RespuestaHttp<>(null, HttpStatus.CONFLICT.value()); // Ya fue procesada
        }

        Usuario usuario = usuariosRepository.findById(dtoInput.getId_usuario());//el que ejecuta la acción

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

                hechosDinamicaRepository.update(solicitud.getHecho());

                if (solicitud.getUsuario().getRol().equals(Rol.VISUALIZADOR)){
                    gestorRoles.VisualizadorAContribuyente(solicitud.getUsuario());
                }
            }
            this.solicitudAgregarHechoRepo.delete(solicitud);
        }
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    public RespuestaHttp<Void> evaluarEliminacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudEliminarHechoRepo.findById(dtoInput.getId_solicitud());

        Usuario usuario = usuariosRepository.findById(dtoInput.getId_usuario());//el que ejecuta la acción

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

        SolicitudHecho solicitud = solicitudModificarHechoRepo.findById(dtoInput.getId_solicitud());
        Usuario usuario = usuariosRepository.findById(dtoInput.getId_usuario());//el que ejecuta la acción

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        else {
            solicitud.setProcesada(true);
            if (dtoInput.getRespuesta()) {
                // El hecho debe modificarse
                solicitud.getHecho().getAtributosHecho().setFechaUltimaActualizacion(ZonedDateTime.now());

                solicitud.getHecho().getAtributosHecho().setModificado(true);
                hechosDinamicaRepository.update(solicitud.getHecho());
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

            output.setId_hecho(mensaje.getId_solicitud_hecho());
            output.setId_mensaje(mensaje.getId());
            output.setId_usuario(mensaje.getId_receptor());
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
        Long id_reporte = reportesHechoRepository.getProxId();
        Reporte reporte = new Reporte(motivo, id_reporte, id_hecho);
        reportesHechoRepository.save(reporte);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

}


