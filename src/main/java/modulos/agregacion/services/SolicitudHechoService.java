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
import org.springframework.http.ResponseEntity;
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
    private final BuscadorPais buscadorPais;
    private final BuscadorProvincia buscadorProvincia;
    private final BuscadorCategoria buscadorCategoria;

    GestorRoles gestorRoles;

    public SolicitudHechoService(ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo,
                                 ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo,
                                 ISolicitudModificarHechoRepository solicitudModificarHechoRepo,
                                 IUsuarioRepository usuariosRepository,
                                 IHechosProxyRepository hechosProxyRepository,
                                 IHechosEstaticaRepository hechosEstaticaRepository,
                                 IHechosDinamicaRepository hechosDinamicaRepository,
                                 IMensajeRepository mensajesRepository,
                                 IReporteHechoRepository reportesHechoRepository, BuscadorPais buscadorPais, BuscadorProvincia buscadorProvincia,
                                 BuscadorCategoria buscadorCategoria) {

        this.solicitudAgregarHechoRepo = solicitudAgregarHechoRepo;
        this.solicitudEliminarHechoRepo = solicitudEliminarHechoRepo;
        this.hechosProxyRepository = hechosProxyRepository;
        this.hechosDinamicaRepository = hechosDinamicaRepository;
        this.hechosEstaticaRepository = hechosEstaticaRepository;
        this.usuariosRepository = usuariosRepository;
        this.solicitudModificarHechoRepo = solicitudModificarHechoRepo;
        this.mensajesRepository = mensajesRepository;
        this.reportesHechoRepository = reportesHechoRepository;
        this.buscadorPais = buscadorPais;
        this.buscadorCategoria = buscadorCategoria;
        this.buscadorProvincia = buscadorProvincia;
        gestorRoles = new GestorRoles();
    }

    public ResponseEntity<?> solicitarSubirHecho(SolicitudHechoInputDTO dto) {

        Usuario usuario = usuariosRepository.findById(dto.getId_usuario()).orElse(null);

        if (usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }

        Pais pais = buscadorPais.buscarOCrear(dto.getPais());
        Provincia provincia = buscadorProvincia.buscarOCrear(dto.getProvincia());
        HechosData hechosData = new HechosData(dto.getTitulo(), dto.getDescripcion(), dto.getTipoContenido(),
                pais, dto.getFechaAcontecimiento(), provincia);

        FuenteDinamica fuenteDinamica = new FuenteDinamica();
        HechoDinamica hecho = fuenteDinamica.crearHecho(hechosData);
        SolicitudSubirHecho solicitudHecho = new SolicitudSubirHecho(usuario, hecho);

        if (DetectorDeSpam.esSpam(dto.getTitulo()) || DetectorDeSpam.esSpam(dto.getDescripcion())) {
            solicitudHecho.setProcesada(true);
            solicitudHecho.setRechazadaPorSpam(true);
            solicitudAgregarHechoRepo.save(solicitudHecho);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Se detectó spam");
        }
        solicitudAgregarHechoRepo.save(solicitudHecho);
        hecho.setUsuario(usuario);

        hechosDinamicaRepository.save(hecho);

        return ResponseEntity.status(HttpStatus.OK).body("Se envió su solicitud para subir hecho");
    }

    //El usuario manda una solicitud para eliminar un hecho -> guardar la solicitud en la base de datos
    // Asumimos que la solicitud de eliminación puede venir de una persona que no haya subido el hecho solicitado
    public ResponseEntity<?> solicitarEliminacionHecho(SolicitudHechoEliminarInputDTO dto){
        Usuario usuario = usuariosRepository.findById(dto.getId_usuario()).orElse(null);

        if (usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }

        HechoDinamica hecho = hechosDinamicaRepository.findById(dto.getId_hecho()).orElse(null);

        if (hecho == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el hecho");
        }

        SolicitudEliminarHecho solicitud = new SolicitudEliminarHecho(usuario, hecho, dto.getJustificacion());

        if (DetectorDeSpam.esSpam(dto.getJustificacion())) {
            // Marcar como rechazada por spam y guardar
            solicitud.setProcesada(true);
            solicitud.setRechazadaPorSpam(true);
            solicitudEliminarHechoRepo.save(solicitud);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Se detectó spam"); // 400 - solicitud rechazada por spam
        }
        solicitudEliminarHechoRepo.save(solicitud);
        return ResponseEntity.status(HttpStatus.OK).body("Se envió su solicitud para eliminar hecho"); // Un admin no debería solicitar eliminar, los elimina directamente
    }

    public ResponseEntity<?> solicitarModificacionHecho(SolicitudHechoModificarInputDTO dto){

        Usuario usuario = usuariosRepository.findById(dto.getId_usuario()).orElse(null);

        if (usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }

        HechoDinamica hecho = hechosDinamicaRepository.findById(dto.getId_hecho()).orElse(null);


        if (hecho == null || !usuario.getId().equals(hecho.getUsuario().getId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El hecho no existe / No tenés permisos para ejecutar esta acción");
        }

        SolicitudModificarHecho solicitud = new SolicitudModificarHecho(usuario, hecho);

        if (DetectorDeSpam.esSpam(dto.getTitulo()) || DetectorDeSpam.esSpam(dto.getDescripcion()))
        {
            solicitud.setProcesada(true);
            solicitud.setRechazadaPorSpam(true);
            solicitudModificarHechoRepo.save(solicitud);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Se detectó spam"); // 400 - solicitud rechazada por spam
        }

        if (ChronoUnit.DAYS.between(hecho.getAtributosHecho().getFechaCarga(), ZonedDateTime.now()) >= 7){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Terminó la fecha límite para solicitar modificar el hecho"); // Error 409: cuando la solicitud es válida, pero no puede procesarse por estado actual del recurso
        }

        hecho.getAtributosHecho().setTitulo(dto.getTitulo());
        hecho.getAtributosHecho().getUbicacion().setPais(buscadorPais.buscarOCrear(dto.getPais()));
        hecho.getAtributosHecho().getUbicacion().setProvincia(buscadorProvincia.buscarOCrear(dto.getProvincia()));
        hecho.getAtributosHecho().setCategoria(buscadorCategoria.buscarOCrear(dto.getCategoria()));
        hecho.getAtributosHecho().setFechaAcontecimiento(FechaParser.parsearFecha(dto.getFechaAcontecimiento()));
        hecho.getAtributosHecho().setContenidoMultimedia(TipoContenido.fromCodigo(dto.getTipoContenido()));
        solicitudModificarHechoRepo.save(solicitud);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    public ResponseEntity<?> evaluarSolicitudSubirHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudAgregarHechoRepo.findById(dtoInput.getId_solicitud()).orElse(null);
        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la solicitud");
        }
        // Verificar que no haya sido procesada ya
        if (solicitud.isProcesada()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La solicitud ya fue procesada"); // Ya fue procesada
        }

        Usuario usuario = usuariosRepository.findById(dtoInput.getId_usuario()).orElse(null);//el que ejecuta la acción

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
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
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity<?> evaluarEliminacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudEliminarHechoRepo.findById(dtoInput.getId_solicitud()).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la solicitud");
        }

        // Verificar que no haya sido procesada ya
        if (solicitud.isProcesada()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La solicitud ya fue procesada"); // Ya fue procesada
        }


        Usuario usuario = usuariosRepository.findById(dtoInput.getId_usuario()).orElse(null);//el que ejecuta la acción

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El usuario no tiene permisos");
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
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> evaluarModificacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudModificarHechoRepo.findById(dtoInput.getId_solicitud()).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la solicitud");
        }

        // Verificar que no haya sido procesada ya
        if (solicitud.isProcesada()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La solicitud ya fue procesada"); // Ya fue procesada
        }

        Usuario usuario = usuariosRepository.findById(dtoInput.getId_usuario()).orElse(null);//el que ejecuta la acción

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El usuario no tiene permisos");
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
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> enviarMensajes(Long id_Usuario){
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

        return ResponseEntity.ok().build();
    }

    // TODO
    /*public ResponseEntity<?> obtenerSolicitudesPendientes() {
        return solicitudEliminarHechoRepo.findAll().stream()
                .filter(s -> !s.isProcesada() && !s.isRechazadaPorSpam())
                .toList();
    }*/

    public ResponseEntity<?> reportarHecho(String motivo, Long id_hecho) {
        Hecho hecho = hechosDinamicaRepository.findById(id_hecho).orElse(null);
        if(hecho == null){
            hecho = hechosEstaticaRepository.findById(id_hecho).orElse(null);
            if(hecho == null){
                hecho = hechosProxyRepository.findById(id_hecho).orElse(null);
                if(hecho == null){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el hecho");
                }
            }
        }
        Reporte reporte = new Reporte(motivo, hecho);
        reportesHechoRepository.save(reporte);
        return ResponseEntity.ok().build();
    }

}


