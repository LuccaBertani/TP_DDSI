package modulos.agregacion.services;

import jakarta.transaction.Transactional;
import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbDinamica.solicitudes.*;
import modulos.agregacion.entities.DbMain.*;
import modulos.agregacion.entities.atributosHecho.AtributosHechoModificar;
import modulos.agregacion.entities.DbMain.projections.SolicitudHechoProjection;
import modulos.agregacion.entities.atributosHecho.TipoContenido;
import modulos.agregacion.repositories.DbDinamica.*;
import modulos.agregacion.repositories.DbMain.*;
import modulos.buscadores.BuscadorPais;
import modulos.buscadores.BuscadorProvincia;
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
import modulos.agregacion.entities.DbMain.usuario.Rol;
import modulos.agregacion.entities.DbMain.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudHechoService {

    private final ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo;
    private final ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo;
    private final ISolicitudModificarHechoRepository solicitudModificarHechoRepo;
    private final IHechosDinamicaRepository hechosDinamicaRepository;
    private final IHechoRepository hechosRepository;
    private final IUsuarioRepository usuariosRepository;
    private final IMensajeRepository mensajesRepository;
    private final IReporteHechoRepository reportesHechoRepository;
    private final ISolicitudRepository solicitudRepository;
    private final IPaisRepository paisRepository;
    private final IProvinciaRepository provinciaRepository;
    private final ICategoriaRepository categoriaRepository;
    private final BuscadorUbicacion buscadorUbicacion;
    private final BuscadorPais buscadorPais;
    private final BuscadorProvincia buscadorProvincia;

    public SolicitudHechoService(ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo, ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo, ISolicitudModificarHechoRepository solicitudModificarHechoRepo, IHechosDinamicaRepository hechosDinamicaRepository, IUsuarioRepository usuariosRepository, IMensajeRepository mensajesRepository, IReporteHechoRepository reportesHechoRepository, ISolicitudRepository solicitudRepository, IPaisRepository paisRepository, IProvinciaRepository provinciaRepository, ICategoriaRepository categoriaRepository, BuscadorUbicacion buscadorUbicacion, IHechoRepository hechosRepository, BuscadorPais buscadorPais, BuscadorProvincia buscadorProvincia) {
        this.solicitudAgregarHechoRepo = solicitudAgregarHechoRepo;
        this.solicitudEliminarHechoRepo = solicitudEliminarHechoRepo;
        this.solicitudModificarHechoRepo = solicitudModificarHechoRepo;
        this.hechosDinamicaRepository = hechosDinamicaRepository;
        this.usuariosRepository = usuariosRepository;
        this.mensajesRepository = mensajesRepository;
        this.reportesHechoRepository = reportesHechoRepository;
        this.solicitudRepository = solicitudRepository;
        this.paisRepository = paisRepository;
        this.provinciaRepository = provinciaRepository;
        this.categoriaRepository = categoriaRepository;
        this.buscadorUbicacion = buscadorUbicacion;
        this.hechosRepository = hechosRepository;
        this.buscadorPais = buscadorPais;
        this.buscadorProvincia = buscadorProvincia;
    }

    private ResponseEntity<?> checkeoAdmin(Long id_usuario){
        Usuario usuario = id_usuario != null ? usuariosRepository.findById(id_usuario).orElse(null) : null;

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }
        return ResponseEntity.ok(usuario);
    }

    private ResponseEntity<?> checkeoContribuyente(Long id_usuario){
        Usuario usuario = id_usuario != null ? usuariosRepository.findById(id_usuario).orElse(null) : null;

        if (usuario == null || !usuario.getRol().equals(Rol.CONTRIBUYENTE)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }
        return ResponseEntity.ok(usuario);
    }

    private ResponseEntity<?> checkeoNoAdmin(Long id_usuario){

        Usuario usuario = id_usuario != null ? usuariosRepository.findById(id_usuario).orElse(null) : null;

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
        Long categoria_id = categoria != null ? categoria.getId() : null;
        Ubicacion ubicacion = buscadorUbicacion.buscarOCrear(pais, provincia);
        Long ubicacion_id = ubicacion != null ? ubicacion.getId() : null;

        HechosData hechosData = new HechosData(dto.getTitulo(), dto.getDescripcion(), dto.getFechaAcontecimiento(),dto.getTipoContenido(), categoria_id, ubicacion_id,
                dto.getLatitud(), dto.getLongitud());

        FuenteDinamica fuenteDinamica = new FuenteDinamica();
        HechoDinamica hecho = fuenteDinamica.crearHecho(hechosData);
        SolicitudSubirHecho solicitudHecho = new SolicitudSubirHecho((Usuario)rta.getBody(), hecho);

        if (DetectorDeSpam.esSpam(dto.getTitulo()) || DetectorDeSpam.esSpam(dto.getDescripcion())) {
            solicitudHecho.setProcesada(true);
            solicitudHecho.setRechazadaPorSpam(true);
            hechosDinamicaRepository.save(hecho);
            solicitudAgregarHechoRepo.save(solicitudHecho);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Se detectó spam");
        }

        Usuario usuario = (Usuario)rta.getBody();

        hecho.setUsuario_id(usuario.getId());
        hecho.getAtributosHecho().setFuente(Fuente.DINAMICA);
        hechosDinamicaRepository.save(hecho);
        solicitudAgregarHechoRepo.save(solicitudHecho);

        return ResponseEntity.status(HttpStatus.OK).body("Se envió su solicitud para subir hecho");
    }

    //El usuario manda una solicitud para eliminar un hecho -> guardar la solicitud en la base de datos
    public ResponseEntity<?> solicitarEliminacionHecho(SolicitudHechoEliminarInputDTO dto){
        // Un admin no debería solicitar eliminar, los elimina directamente
        ResponseEntity<?> rta = checkeoContribuyente(dto.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        // El hecho debe estar asociado al usuario
        HechoDinamica hecho = hechosDinamicaRepository.findByIdAndUsuario(dto.getId_hecho(), dto.getId_usuario()).orElse(null);
        if (hecho == null){
            // Puede ser que se haya encontrado el hecho pero que el usuario no esté asociado al hecho
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tenés permisos para ejecutar esta acción");
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
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity<?> solicitarModificacionHecho(SolicitudHechoModificarInputDTO dto){

        ResponseEntity<?> rta = checkeoContribuyente(dto.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        Usuario usuario = (Usuario)rta.getBody();

        if (usuario == null){
            return rta;
        }

        HechoDinamica hecho = hechosDinamicaRepository.findByIdAndUsuario(dto.getId_hecho(), usuario.getId()).orElse(null);

        if (hecho == null){
            // El hecho no existe o el usuario no tiene permiso
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
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

        AtributosHechoModificar atributos = new AtributosHechoModificar();
        Optional.ofNullable(dto.getTitulo()).ifPresent(atributos::setTitulo);

        if(dto.getId_pais() !=null || dto.getId_provincia() != null){
            Pais pais = buscadorPais.buscar(dto.getId_pais());
            Provincia provincia = buscadorProvincia.buscar(dto.getId_provincia());
            Ubicacion ubicacion = buscadorUbicacion.buscarOCrear(pais, provincia);
            atributos.setUbicacion_id(ubicacion != null ? ubicacion.getId() : null);
        }
        // TODO: En todos los casos, chequear que latitud y longitud VENGAN JUNTOS EN TODOS LOS CASOS
        if (dto.getLongitud() != null && dto.getLatitud()!=null){
            atributos.setLatitud(dto.getLatitud());
            atributos.setLongitud(dto.getLongitud());
        }

        Optional.ofNullable(dto.getId_categoria()).flatMap(categoriaRepository::findById).
                ifPresent(categoria -> atributos.setCategoria_id(categoria.getId()));


        Optional.ofNullable(dto.getFechaAcontecimiento()).ifPresent(fechaStr -> {
            atributos.setFechaAcontecimiento(FechaParser.parsearFecha(fechaStr));
        });

        Optional.ofNullable(dto.getTipoContenido()).ifPresent(contenido -> {
           atributos.setContenidoMultimedia(TipoContenido.fromCodigo(dto.getTipoContenido()));
        });

        Optional.ofNullable(dto.getDescripcion()).ifPresent(atributos::setDescripcion);
        hecho.getAtributosHechoAModificar().add(atributos);
        solicitud.setAtributosAshei(atributos);
        solicitudModificarHechoRepo.save(solicitud);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    // @Transacional:
    // Así, la SolicitudHecho que traés con findById queda managed durante el method y all lo que le modifiques
    //  (y a sus asociaciones cargadas) se hace UPDATE al hacer commit, sin llamar a save(...).
    @Transactional
    public ResponseEntity<?> evaluarSolicitudSubirHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudRepository.findByIdAndProcesadaFalse(dtoInput.getId_solicitud()).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la solicitud");
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

            if (solicitud.getUsuario() != null){
                solicitud.getUsuario().incrementarHechosSubidos();
                if (solicitud.getUsuario().getRol().equals(Rol.VISUALIZADOR)){
                    GestorRoles.VisualizadorAContribuyente(solicitud.getUsuario());
                }
            }

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    public ResponseEntity<?> evaluarEliminacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        ResponseEntity<?> rta = checkeoAdmin(dtoInput.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        SolicitudHecho solicitud = solicitudRepository.findByIdAndProcesadaFalse(dtoInput.getId_solicitud()).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la solicitud");
        }

        solicitud.setProcesada(true);
        if (dtoInput.getRespuesta()) {
            // No va a haber null pointer exception porque sí o sí hay un usuario asociado al hecho que se solicita eliminar
            solicitud.getUsuario().disminuirHechosSubidos();
            solicitud.getHecho().getAtributosHecho().setModificado(true);

            if (solicitud.getUsuario().getCantHechosSubidos() == 0){
                GestorRoles.ContribuyenteAVisualizador(solicitud.getUsuario());
            }
        }

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> evaluarModificacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        ResponseEntity<?> rta = checkeoAdmin(dtoInput.getId_usuario());

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        SolicitudModificarHecho solicitud = (SolicitudModificarHecho) solicitudRepository.findByIdAndProcesadaFalse(dtoInput.getId_solicitud()).orElse(null);
        

        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la solicitud");
        }

        solicitud.setProcesada(true);

        if (dtoInput.getRespuesta()) {
            // El hecho debe modificarse
            this.setearModificadoAOficial(solicitud.getHecho(), solicitud.getAtributosAshei());
            solicitud.getHecho().getAtributosHecho().setFechaUltimaActualizacion(ZonedDateTime.now());
            solicitud.getHecho().getAtributosHecho().setModificado(true);
        }
        else{
            if (dtoInput.getMensaje() != null){
                return this.enviarMensaje(solicitud.getUsuario(),solicitud, dtoInput.getMensaje());
            }
        }
        return ResponseEntity.ok().build();
    }

    private void setearModificadoAOficial(Hecho hecho, AtributosHechoModificar atributos){

        Optional.ofNullable(atributos.getCategoria_id()).ifPresent(hecho.getAtributosHecho()::setCategoria_id);
        Optional.ofNullable(atributos.getDescripcion()).ifPresent(hecho.getAtributosHecho()::setDescripcion);
        Optional.ofNullable(atributos.getFechaAcontecimiento()).ifPresent(hecho.getAtributosHecho()::setFechaAcontecimiento);
        Optional.ofNullable(atributos.getTitulo()).ifPresent(hecho.getAtributosHecho()::setTitulo);
        Optional.ofNullable(atributos.getUbicacion_id()).ifPresent(hecho.getAtributosHecho()::setUbicacion_id);
        Optional.ofNullable(atributos.getContenidoMultimedia()).ifPresent(hecho.getAtributosHecho()::setContenidoMultimedia);
        Optional.ofNullable(atributos.getLatitud()).ifPresent(hecho.getAtributosHecho()::setLatitud);
        Optional.ofNullable(atributos.getLongitud()).ifPresent(hecho.getAtributosHecho()::setLongitud);
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

        ResponseEntity<?> rta = checkeoAdmin(id_usuario);

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }


        List<SolicitudHecho> solicitudesHechos = solicitudRepository.findAll();
        List<SolicitudHechoOutputDTO> solicitudHechoOutputDTOS = new ArrayList<>();
        for (SolicitudHecho solicitud: solicitudesHechos){
            SolicitudHechoOutputDTO dto = SolicitudHechoOutputDTO.builder()
                    .id(solicitud.getId())
                    .usuarioId(
                            Optional.ofNullable(solicitud.getUsuario())
                                    .map(Usuario::getId)
                                    .orElse(null)   // o un valor por defecto, si no querés null
                    )
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
        ResponseEntity<?> rta = checkeoAdmin(id_usuario);

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
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
        Hecho hecho = hechosRepository.findById(id_hecho).orElse(null);
        if(hecho == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el hecho");
        }
        Reporte reporte = new Reporte(motivo, id_hecho);
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


