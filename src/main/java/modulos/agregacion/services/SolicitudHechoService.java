package modulos.agregacion.services;

import jakarta.validation.Valid;
import modulos.agregacion.entities.*;
import modulos.agregacion.entities.projections.SolicitudHechoProjection;
import modulos.agregacion.entities.solicitudes.*;
import modulos.agregacion.repositories.*;
import modulos.buscadores.BuscadorProvincia;
import modulos.agregacion.entities.fuentes.Origen;
import modulos.shared.dtos.input.SolicitudHechoEliminarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoEvaluarInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.input.SolicitudHechoModificarInputDTO;
import modulos.shared.dtos.output.MensajeOutputDTO;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorPais;
import modulos.shared.dtos.output.SolicitudHechoOutputDTO;
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
    private final ISolicitudRepository solicitudRepository;
    private final IPaisRepository paisRepository;
    private final IProvinciaRepository provinciaRepository;
    private final ICategoriaRepository categoriaRepository;

    GestorRoles gestorRoles;

    public SolicitudHechoService(ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo, ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo,
                                 ISolicitudModificarHechoRepository solicitudModificarHechoRepo, IHechosProxyRepository hechosProxyRepository,
                                 IHechosEstaticaRepository hechosEstaticaRepository, IHechosDinamicaRepository hechosDinamicaRepository,
                                 IUsuarioRepository usuariosRepository, IMensajeRepository mensajesRepository, IReporteHechoRepository reportesHechoRepository,
                                 BuscadorPais buscadorPais, BuscadorProvincia buscadorProvincia, BuscadorCategoria buscadorCategoria,
                                 ISolicitudRepository solicitudRepository, IPaisRepository paisRepository, IProvinciaRepository provinciaRepository,
                                 ICategoriaRepository categoriaRepository) {
        this.solicitudAgregarHechoRepo = solicitudAgregarHechoRepo;
        this.solicitudEliminarHechoRepo = solicitudEliminarHechoRepo;
        this.solicitudModificarHechoRepo = solicitudModificarHechoRepo;
        this.hechosProxyRepository = hechosProxyRepository;
        this.hechosEstaticaRepository = hechosEstaticaRepository;
        this.hechosDinamicaRepository = hechosDinamicaRepository;
        this.usuariosRepository = usuariosRepository;
        this.mensajesRepository = mensajesRepository;
        this.reportesHechoRepository = reportesHechoRepository;
        this.buscadorPais = buscadorPais;
        this.buscadorProvincia = buscadorProvincia;
        this.buscadorCategoria = buscadorCategoria;
        this.solicitudRepository = solicitudRepository;
        this.paisRepository = paisRepository;
        this.provinciaRepository = provinciaRepository;
        this.categoriaRepository = categoriaRepository;
        this.gestorRoles = new GestorRoles();
    }


    public ResponseEntity<?> solicitarSubirHecho(SolicitudHechoInputDTO dto) {

        Usuario usuario = usuariosRepository.findById(dto.getId_usuario()).orElse(null);

        if (usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }

        // TODO: COMO HAY QUE DEJAR LOS PAISES PROVINCIAS Y CATEGORIAS EN DINAMICA
        Pais pais = paisRepository.findById(dto.getPais()).orElse(null);
        Provincia provincia = provinciaRepository.findById(dto.getProvincia()).orElse(null);
        Categoria categoria = categoriaRepository.findById(dto.getCategoria()).orElse(null);
        HechosData hechosData = new HechosData(dto.getTitulo(), dto.getDescripcion(), dto.getTipoContenido(),
                pais, dto.getFechaAcontecimiento(), provincia, categoria);

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
        hecho.getAtributosHecho().getUbicacion().setPais(paisRepository.findById(dto.getPais()).orElse(null));
        hecho.getAtributosHecho().getUbicacion().setProvincia(provinciaRepository.findById(dto.getProvincia()).orElse(null));
        hecho.getAtributosHecho().setCategoria(categoriaRepository.findById(dto.getCategoria()).orElse(null));
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
        solicitud.setProcesada(true);
        if (dtoInput.getRespuesta()) {
            // El hecho debe modificarse
            solicitud.getHecho().getAtributosHecho().setFechaUltimaActualizacion(ZonedDateTime.now());

            solicitud.getHecho().getAtributosHecho().setModificado(true);
            hechosDinamicaRepository.save(solicitud.getHecho());
        }

        solicitudModificarHechoRepo.delete(solicitud);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> enviarMensaje(Long id_emisor, Long id_receptor, Long id_solicitud, String texto){

        Usuario usuario = usuariosRepository.findById(id_emisor).orElse(null);//el que ejecuta la acción

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el emisor");
        }

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }

        Usuario usuario2 = usuariosRepository.findById(id_receptor).orElse(null);

        if (usuario2 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el receptor");
        }

        SolicitudHecho solicitudHecho = solicitudRepository.findById(id_solicitud).orElse(null);

        if (solicitudHecho == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la solicitud");
        }

        if (!solicitudHecho.getUsuario().getId().equals(id_receptor)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El receptor no envió la solicitud indicada");
        }

        Mensaje mensaje = new Mensaje();
        mensaje.setSolicitud_hecho(solicitudHecho);
        mensaje.setTextoMensaje(texto);
        mensaje.setReceptor(usuario2);
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


