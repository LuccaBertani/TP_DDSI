package modulos.agregacion.services;

import jakarta.transaction.Transactional;
import modulos.agregacion.entities.*;
import modulos.agregacion.entities.projections.SolicitudHechoProjection;
import modulos.agregacion.entities.solicitudes.*;
import modulos.agregacion.repositories.*;
import modulos.buscadores.BuscadorUbicacion;
import modulos.shared.dtos.input.SolicitudHechoEliminarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoEvaluarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.input.SolicitudHechoModificarInputDTO;
import modulos.shared.dtos.output.MensajeOutputDTO;
import modulos.shared.dtos.output.SolicitudHechoOutputDTO;
import modulos.shared.utils.DetectorDeSpam;
import modulos.shared.utils.FechaParser;
import modulos.agregacion.entities.fuentes.FuenteDinamica;
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

    private final ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo;
    private final ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo;
    private final ISolicitudModificarHechoRepository solicitudModificarHechoRepo;
    private final IHechosProxyRepository hechosProxyRepository;
    private final IHechosEstaticaRepository hechosEstaticaRepository;
    private final IHechosDinamicaRepository hechosDinamicaRepository;
    private final IUsuarioRepository usuariosRepository;
    private final IMensajeRepository mensajesRepository;
    private final IReporteHechoRepository reportesHechoRepository;
    private final ISolicitudRepository solicitudRepository;
    private final IPaisRepository paisRepository;
    private final IProvinciaRepository provinciaRepository;
    private final ICategoriaRepository categoriaRepository;
    private final BuscadorUbicacion buscadorUbicacion;

    public SolicitudHechoService(ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo, ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo, ISolicitudModificarHechoRepository solicitudModificarHechoRepo, IHechosProxyRepository hechosProxyRepository, IHechosEstaticaRepository hechosEstaticaRepository, IHechosDinamicaRepository hechosDinamicaRepository, IUsuarioRepository usuariosRepository, IMensajeRepository mensajesRepository, IReporteHechoRepository reportesHechoRepository, ISolicitudRepository solicitudRepository, IPaisRepository paisRepository, IProvinciaRepository provinciaRepository, ICategoriaRepository categoriaRepository, BuscadorUbicacion buscadorUbicacion) {
        this.solicitudAgregarHechoRepo = solicitudAgregarHechoRepo;
        this.solicitudEliminarHechoRepo = solicitudEliminarHechoRepo;
        this.solicitudModificarHechoRepo = solicitudModificarHechoRepo;
        this.hechosProxyRepository = hechosProxyRepository;
        this.hechosEstaticaRepository = hechosEstaticaRepository;
        this.hechosDinamicaRepository = hechosDinamicaRepository;
        this.usuariosRepository = usuariosRepository;
        this.mensajesRepository = mensajesRepository;
        this.reportesHechoRepository = reportesHechoRepository;
        this.solicitudRepository = solicitudRepository;
        this.paisRepository = paisRepository;
        this.provinciaRepository = provinciaRepository;
        this.categoriaRepository = categoriaRepository;
        this.buscadorUbicacion = buscadorUbicacion;
    }

    private ResponseEntity<?> checkeoAdmin(Long id_usuario){
        Usuario usuario = usuariosRepository.findById(id_usuario).orElse(null);

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }
        return ResponseEntity.ok(usuario);
    }

    private ResponseEntity<?> checkeoNoAdmin(Long id_usuario){

        Usuario usuario = usuariosRepository.findById(id_usuario).orElse(null);

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
    }

    public ResponseEntity<?> solicitarSubirHecho(SolicitudHechoInputDTO dto) {

        // Los visualizadores o contribuyentes llaman al metodo, no los admins
        ResponseEntity<?> rta = this.checkeoNoAdmin(dto.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        Pais pais = dto.getId_pais() != null ? paisRepository.findById(dto.getId_pais()).orElse(null) : null;
        Provincia provincia = dto.getId_provincia() != null ? provinciaRepository.findById(dto.getId_provincia()).orElse(null) : null;
        Categoria categoria = dto.getId_categoria() != null ? categoriaRepository.findById(dto.getId_categoria()).orElse(null) : null;

        Ubicacion ubicacion = buscadorUbicacion.buscarOCrear(pais, provincia);

        HechosData hechosData = new HechosData(dto.getTitulo(), dto.getDescripcion(), dto.getTipoContenido(), dto.getFechaAcontecimiento(), categoria, ubicacion);

        FuenteDinamica fuenteDinamica = new FuenteDinamica();
        HechoDinamica hecho = fuenteDinamica.crearHecho(hechosData);
        SolicitudSubirHecho solicitudHecho = new SolicitudSubirHecho((Usuario)rta.getBody(), hecho);

        if (DetectorDeSpam.esSpam(dto.getTitulo()) || DetectorDeSpam.esSpam(dto.getDescripcion())) {
            solicitudHecho.setProcesada(true);
            solicitudHecho.setRechazadaPorSpam(true);
            solicitudAgregarHechoRepo.save(solicitudHecho);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Se detectó spam");
        }

        hecho.setUsuario((Usuario)rta.getBody());
        hecho.getAtributosHecho().setFechaCarga(ZonedDateTime.now());
        hechosDinamicaRepository.save(hecho);
        solicitudAgregarHechoRepo.save(solicitudHecho);

        return ResponseEntity.status(HttpStatus.OK).body("Se envió su solicitud para subir hecho");
    }

    //El usuario manda una solicitud para eliminar un hecho -> guardar la solicitud en la base de datos
    // Asumimos que la solicitud de eliminación puede venir de una persona que no haya subido el hecho solicitado
    public ResponseEntity<?> solicitarEliminacionHecho(SolicitudHechoEliminarInputDTO dto){
        ResponseEntity<?> rta = checkeoAdmin(dto.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        HechoDinamica hecho = hechosDinamicaRepository.findById(dto.getId_hecho()).orElse(null);

        if (hecho == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el hecho");
        }

        SolicitudEliminarHecho solicitud = new SolicitudEliminarHecho((Usuario)rta.getBody(), hecho, dto.getJustificacion());

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

        ResponseEntity<?> rta = checkeoAdmin(dto.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        Usuario usuario = (Usuario)rta.getBody();

        if (usuario == null){
            return rta;
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

        Pais pais = paisRepository.findById(dto.getId_pais()).orElse(null);
        Provincia provincia = provinciaRepository.findById(dto.getId_provincia()).orElse(null);
        Ubicacion ubicacion = buscadorUbicacion.buscarOCrear(pais, provincia);
        hecho.getAtributosHecho().setUbicacion(ubicacion);

        hecho.getAtributosHecho().setCategoria(categoriaRepository.findById(dto.getId_categoria()).orElse(null));
        hecho.getAtributosHecho().setFechaAcontecimiento(FechaParser.parsearFecha(dto.getFechaAcontecimiento()));
        hecho.getAtributosHecho().setContenidoMultimedia(TipoContenido.fromCodigo(dto.getTipoContenido()));
        solicitudModificarHechoRepo.save(solicitud);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    // @Transacional:
    // Así, la SolicitudHecho que traés con findById queda managed durante el method y all lo que le modifiques
    //  (y a sus asociaciones cargadas) se hace UPDATE al hacer commit, sin llamar a save(...).
    @Transactional
    public ResponseEntity<?> evaluarSolicitudSubirHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudAgregarHechoRepo.findById(dtoInput.getId_solicitud()).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la solicitud");
        }

        ResponseEntity<?> rta = checkeoAdmin(dtoInput.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        // Marcar como procesada
        solicitud.setProcesada(true);
        if (dtoInput.getRespuesta()) {
            solicitud.getHecho().setActivo(true);
            solicitud.getHecho().getAtributosHecho().setFechaCarga(ZonedDateTime.now());
            solicitud.getHecho().getAtributosHecho().setFechaUltimaActualizacion(solicitud.getHecho().getAtributosHecho().getFechaCarga()); // Nueva fecha de modificación
            solicitud.getUsuario().incrementarHechosSubidos();

            if (solicitud.getUsuario().getRol().equals(Rol.VISUALIZADOR)){
                GestorRoles.VisualizadorAContribuyente(solicitud.getUsuario());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity<?> evaluarEliminacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudEliminarHechoRepo.findById(dtoInput.getId_solicitud()).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la solicitud");
        }

        ResponseEntity<?> rta = checkeoAdmin(dtoInput.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        solicitud.setProcesada(true);
        if (dtoInput.getRespuesta()) {
            solicitud.getUsuario().disminuirHechosSubidos();
            hechosDinamicaRepository.delete(solicitud.getHecho());

            if (solicitud.getUsuario().getCantHechosSubidos() == 0){
                GestorRoles.ContribuyenteAVisualizador(solicitud.getUsuario());
            }
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> evaluarModificacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudModificarHechoRepo.findById(dtoInput.getId_solicitud()).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la solicitud");
        }

        ResponseEntity<?> rta = checkeoAdmin(dtoInput.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        solicitud.setProcesada(true);
        if (dtoInput.getRespuesta()) {
            // El hecho debe modificarse
            solicitud.getHecho().getAtributosHecho().setFechaUltimaActualizacion(ZonedDateTime.now());

            solicitud.getHecho().getAtributosHecho().setModificado(true);
            hechosDinamicaRepository.save(solicitud.getHecho());
        }
        else{
            if (dtoInput.getMensaje() != null){
                return this.enviarMensaje(solicitud.getUsuario(),solicitud, dtoInput.getMensaje());
            }
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> enviarMensaje(Usuario usuario, SolicitudHecho solicitudHecho, String texto){

        Mensaje mensaje = new Mensaje();
        mensaje.setSolicitud_hecho(solicitudHecho);
        mensaje.setTextoMensaje(texto);
        mensaje.setReceptor(usuario);
        mensajesRepository.save(mensaje);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getAllSolicitudes(Long id_usuario) {
        Usuario usuario = usuariosRepository.findById(id_usuario).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El usuario no tiene permisos");
        }

        List<SolicitudHecho> solicitudesHechos = solicitudRepository.findAll();
        List<SolicitudHechoOutputDTO> solicitudHechoOutputDTOS = new ArrayList<>();
        for (SolicitudHecho solicitud: solicitudesHechos){
            SolicitudHechoOutputDTO dto = SolicitudHechoOutputDTO.builder()
                    .id(solicitud.getId())
                    .usuarioId(solicitud.getUsuario().getId())
                    .hechoId(solicitud.getHecho().getId())
                    .justificacion(solicitud.getJustificacion())
                    .procesada(solicitud.isProcesada())
                    .rechazadaPorSpam(solicitud.isRechazadaPorSpam())
                    .build();
            solicitudHechoOutputDTOS.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(solicitudHechoOutputDTOS);
    }

    public ResponseEntity<?> obtenerSolicitudesPendientes(Long id_usuario) {
        Usuario usuario = usuariosRepository.findById(id_usuario).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El usuario no tiene permisos");
        }

        List<SolicitudHechoProjection> solicitudesHechosProjection = solicitudRepository.obtenerSolicitudesPendientes();

        List<SolicitudHechoOutputDTO> solicitudHechoOutputDTOS = new ArrayList<>();
        for (SolicitudHechoProjection solicitud: solicitudesHechosProjection){
            SolicitudHechoOutputDTO dto = SolicitudHechoOutputDTO.builder()
                    .id(solicitud.getId())
                    .usuarioId(solicitud.getUsuarioId())
                    .hechoId(solicitud.getHechoId())
                    .justificacion(solicitud.getJustificacion())
                    .procesada(solicitud.getProcesada())
                    .rechazadaPorSpam(solicitud.getRechazadaPorSpam())
                    .build();
            solicitudHechoOutputDTOS.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(solicitudHechoOutputDTOS);
    }

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

    public ResponseEntity<?> obtenerMensajes(Long id_receptor) {
        Usuario usuario = usuariosRepository.findById(id_receptor).orElse(null);
        if (usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        List<Mensaje> mensajes = mensajesRepository.findByReceptor(usuario);
        List<MensajeOutputDTO> mensajesOutputDTOS = new ArrayList<>();
        for (Mensaje mensaje: mensajes){
            MensajeOutputDTO dto = MensajeOutputDTO.builder()
                    .id_usuario(id_receptor)
                    .id_solicitud_hecho(mensaje.getSolicitud_hecho().getId())
                    .id_mensaje(mensaje.getId())
                    .mensaje(mensaje.getTextoMensaje())
                    .build();

            mensajesOutputDTOS.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(mensajesOutputDTOS);

    }
}


