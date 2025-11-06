package modulos.agregacion.services;

import io.jsonwebtoken.Jwt;
import jakarta.transaction.Transactional;
import modulos.JwtClaimExtractor;
import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbDinamica.solicitudes.*;
import modulos.agregacion.entities.DbEstatica.HechoEstatica;
import modulos.agregacion.entities.DbMain.*;
import modulos.agregacion.entities.DbMain.hechoRef.HechoRef;
import modulos.agregacion.entities.DbProxy.HechoProxy;
import modulos.agregacion.entities.atributosHecho.AtributosHechoModificar;
import modulos.agregacion.entities.DbMain.projections.SolicitudHechoProjection;
import modulos.agregacion.entities.atributosHecho.ContenidoMultimedia;
import modulos.agregacion.entities.atributosHecho.TipoContenido;
import modulos.agregacion.repositories.DbDinamica.*;
import modulos.agregacion.repositories.DbEstatica.IHechosEstaticaRepository;
import modulos.agregacion.repositories.DbMain.*;
import modulos.agregacion.repositories.DbProxy.IHechosProxyRepository;
import modulos.buscadores.BuscadorPais;
import modulos.buscadores.BuscadorProvincia;
import modulos.buscadores.BuscadorUbicacion;
import modulos.shared.dtos.input.*;
import modulos.shared.dtos.output.MensajeOutputDTO;
import modulos.shared.dtos.output.ReporteHechoOutputDTO;
import modulos.shared.dtos.output.RolCambiadoDTO;
import modulos.shared.dtos.output.SolicitudHechoOutputDTO;
import modulos.shared.utils.DetectorDeSpam;
import modulos.shared.utils.FechaParser;
import modulos.agregacion.entities.fuentes.FuenteDinamica;
import modulos.shared.utils.GestorArchivos;
import modulos.shared.utils.GestorRoles;
import modulos.agregacion.entities.DbMain.usuario.Rol;
import modulos.agregacion.entities.DbMain.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final IHechosEstaticaRepository hechosEstaticaRepository;
    private final IHechosProxyRepository hechosProxyRepository;
    private final IHechoRefRepository hechoRefRepository;

    public SolicitudHechoService(ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo, IHechoRefRepository hechoRefRepository, IHechosProxyRepository hechosProxyRepository, ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo, ISolicitudModificarHechoRepository solicitudModificarHechoRepo, IHechosDinamicaRepository hechosDinamicaRepository, IUsuarioRepository usuariosRepository, IMensajeRepository mensajesRepository, IReporteHechoRepository reportesHechoRepository, ISolicitudRepository solicitudRepository, IPaisRepository paisRepository, IProvinciaRepository provinciaRepository, ICategoriaRepository categoriaRepository, BuscadorUbicacion buscadorUbicacion, BuscadorPais buscadorPais, BuscadorProvincia buscadorProvincia, IHechosEstaticaRepository hechosEstaticaRepository) {
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
        this.buscadorPais = buscadorPais;
        this.buscadorProvincia = buscadorProvincia;
        this.hechosEstaticaRepository = hechosEstaticaRepository;
        this.hechosProxyRepository = hechosProxyRepository;
        this.hechoRefRepository = hechoRefRepository;
    }

    private ResponseEntity<?> checkeoAdmin(String username){
        Usuario usuario = username != null ? usuariosRepository.findByNombreDeUsuario(username).orElse(null) : null;

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }
        return ResponseEntity.ok(usuario);
    }

    private ResponseEntity<?> checkeoContribuyente(String username){
        Usuario usuario = username != null ? usuariosRepository.findByNombreDeUsuario(username).orElse(null) : null;

        if (usuario == null || !usuario.getRol().equals(Rol.CONTRIBUYENTE)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }
        return ResponseEntity.ok(usuario);
    }

    private ResponseEntity<?> checkeoNoAdmin(String username){

        Usuario usuario = username != null ? usuariosRepository.findByNombreDeUsuario(username).orElse(null) : null;

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
    }

    @Transactional
    public ResponseEntity<?> solicitarSubirHecho(SolicitudHechoInputDTO dto, String username){
        // Los visualizadores o contribuyentes llaman al metodo, no los admins
        Usuario usuario = null;
        usuario = usuariosRepository.findByNombreDeUsuario(username).orElse(null);
        if (username!=null && !username.isEmpty()){
            ResponseEntity<?> rta = this.checkeoNoAdmin(username);

            if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
                return rta;
            }

            usuario = (Usuario) rta.getBody();
        }


        Pais pais = dto.getId_pais() != null ? paisRepository.findById(dto.getId_pais()).orElse(null) : null;
        Provincia provincia = dto.getId_provincia() != null ? provinciaRepository.findById(dto.getId_provincia()).orElse(null) : null;
        Categoria categoria = dto.getId_categoria() != null ? categoriaRepository.findById(dto.getId_categoria()).orElse(null) : null;
        Long categoria_id = categoria != null ? categoria.getId() : null;
        Ubicacion ubicacion = buscadorUbicacion.buscarOCrear(pais, provincia);
        Long ubicacion_id = ubicacion != null ? ubicacion.getId() : null;

        List<ContenidoMultimedia> contenidosMultimedia = new ArrayList<>();

        if (dto.getContenidosMultimedia() != null){
            for(MultipartFile file : dto.getContenidosMultimedia()) {
                try {
                    String url = GestorArchivos.guardarArchivo(file);

                    ContenidoMultimedia contenidoMultimedia = new ContenidoMultimedia();

                    contenidoMultimedia.setUrl(url);
                    contenidoMultimedia.almacenarTipoDeArchivo(file.getContentType());
                    contenidosMultimedia.add(contenidoMultimedia);
                } catch (IOException ignore) {
                }
            }
        }



        FuenteDinamica fuenteDinamica = new FuenteDinamica();
        HechoDinamica hecho = fuenteDinamica.crearHecho(dto, contenidosMultimedia, categoria_id, ubicacion_id);

        SolicitudSubirHecho solicitudHecho = new SolicitudSubirHecho();
        solicitudHecho.setFecha(ZonedDateTime.now());
        if(usuario!=null) {
            solicitudHecho.setUsuario_id(usuario.getId());
            hecho.setUsuario_id(usuario.getId());

        } else{
            solicitudHecho.setUsuario_id(null);
            hecho.setUsuario_id(null);
        }

        if (DetectorDeSpam.esSpam(dto.getTitulo()) || DetectorDeSpam.esSpam(dto.getDescripcion())) {
            System.out.println("SOY UNA x AL IGUAL QUE EL DETECTOR DE SPAM");
            solicitudHecho.setProcesada(true);
            solicitudHecho.setRechazadaPorSpam(true);
            hechosDinamicaRepository.saveAndFlush(hecho);
            solicitudHecho.setHecho(hecho);
            solicitudAgregarHechoRepo.save(solicitudHecho);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Se detectó spam");
        }

        hecho.getAtributosHecho().setFuente(Fuente.DINAMICA);

        hechosDinamicaRepository.save(hecho);
        solicitudHecho.setHecho(hecho);
        solicitudAgregarHechoRepo.save(solicitudHecho);
        hechoRefRepository.save(new HechoRef(hecho.getId(), hecho.getAtributosHecho().getFuente()));

        return ResponseEntity.status(HttpStatus.OK).body("Se envió su solicitud para subir hecho");
    }

    //El usuario manda una solicitud para eliminar un hecho -> guardar la solicitud en la base de datos
    public ResponseEntity<?> solicitarEliminacionHecho(SolicitudHechoEliminarInputDTO dto, Jwt principal){
        // Un admin no debería solicitar eliminar, los elimina directamente

            ResponseEntity<?> rta = this.checkeoNoAdmin(JwtClaimExtractor.getUsernameFromToken(principal));
            Usuario usuario = (Usuario) rta.getBody();
            if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
                return rta;
            }

        // El hecho debe estar asociado al usuario
        HechoDinamica hecho = hechosDinamicaRepository.findByIdAndUsuario(dto.getId_hecho(), usuario.getId()).orElse(null);
        if (hecho == null){
            // Puede ser que se haya encontrado el hecho pero que el usuario no esté asociado al hecho
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }

        SolicitudEliminarHecho solicitud = new SolicitudEliminarHecho(usuario.getId(), hecho, dto.getJustificacion());
        solicitud.setFecha(ZonedDateTime.now());
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

    public ResponseEntity<?> solicitarModificacionHecho(SolicitudHechoModificarInputDTO dto, Jwt principal){

        
        ResponseEntity<?> rta = checkeoContribuyente(JwtClaimExtractor.getUsernameFromToken(principal));

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

        SolicitudModificarHecho solicitud = new SolicitudModificarHecho(usuario.getId(), hecho);
        solicitud.setFecha(ZonedDateTime.now());
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

        if (dto.getLongitud() != null && dto.getLatitud()!=null){
            atributos.setLatitud(dto.getLatitud());
            atributos.setLongitud(dto.getLongitud());
        }

        Optional.ofNullable(dto.getId_categoria()).flatMap(categoriaRepository::findById).
                ifPresent(categoria -> atributos.setCategoria_id(categoria.getId()));


        Optional.ofNullable(dto.getFechaAcontecimiento()).ifPresent(fechaStr -> {
            atributos.setFechaAcontecimiento(FechaParser.parsearFecha(fechaStr));
        });

        List<ContenidoMultimedia> contenidosMultimediaParaAgregar = new ArrayList<>();

        if(dto.getContenidosMultimediaParaAgregar() != null) {
            for (MultipartFile file : dto.getContenidosMultimediaParaAgregar()) {
                try {
                    String url = GestorArchivos.guardarArchivo(file);

                    ContenidoMultimedia contenidoMultimedia = new ContenidoMultimedia();

                    contenidoMultimedia.setUrl(url);
                    contenidoMultimedia.almacenarTipoDeArchivo(file.getContentType());
                    contenidosMultimediaParaAgregar.add(contenidoMultimedia);
                } catch(IOException ignore){
                }
            }

            atributos.setContenidoMultimediaAgregar(contenidosMultimediaParaAgregar);

        }
        Optional.ofNullable(dto.getContenidosMultimediaAEliminar()).ifPresent(atributos::setContenidoMultimediaEliminar);
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
    public ResponseEntity<?> evaluarSolicitudSubirHecho(SolicitudHechoEvaluarInputDTO dtoInput, String username) {

        RolCambiadoDTO dto = new RolCambiadoDTO();
        dto.setRolModificado(false);

        SolicitudHecho solicitud = solicitudRepository.findByIdAndProcesadaFalse(dtoInput.getId_solicitud()).orElse(null);

        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la solicitud");
        }

        ResponseEntity<?> rta = checkeoAdmin(username);

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        // Marcar como procesada
        solicitud.setProcesada(true);
        Usuario usuario = usuariosRepository.findById(solicitud.getUsuario_id()).orElse(null);
        if (dtoInput.getRespuesta()) {
            solicitud.getHecho().setActivo(true);
            solicitud.getHecho().getAtributosHecho().setModificado(true);
            solicitud.getHecho().getAtributosHecho().setFechaCarga(ZonedDateTime.now());
            solicitud.getHecho().getAtributosHecho().setFechaUltimaActualizacion(solicitud.getHecho().getAtributosHecho().getFechaCarga()); // Nueva fecha de modificación

            if (solicitud.getUsuario_id() != null){
                // El usuario va a existir si o si porque ya se verificó cuando solicitó subir un hecho, pero x si pide borrar la cuenta hago el chequeo antes
                if (usuario != null){
                    usuario.incrementarHechosSubidos();
                    Mensaje mensaje = new Mensaje();
                    mensaje.setSolicitud_hecho_id(solicitud.getId());
                    mensaje.setReceptor(usuario);
                    mensaje.setTextoMensaje("Se aceptó su hecho de título " + solicitud.getHecho().getAtributosHecho().getTitulo());
                    mensajesRepository.save(mensaje);
                    if (usuario.getRol().equals(Rol.VISUALIZADOR)){
                        dto.setRol(Rol.CONTRIBUYENTE);
                        dto.setRolModificado(true);
                        dto.setUsername(usuario.getNombreDeUsuario());
                        GestorRoles.VisualizadorAContribuyente(usuario);
                    }
                }

            }

        }
        else{
            if (solicitud.getUsuario_id() != null) {
                Mensaje mensaje = new Mensaje();
                mensaje.setSolicitud_hecho_id(solicitud.getId());
                mensaje.setReceptor(usuario);
                mensaje.setTextoMensaje("Se rechazó su solicitud de subida del hecho de título " + solicitud.getHecho().getAtributosHecho().getTitulo()
                + ".\nJustificacion: " + solicitud.getJustificacion());
                mensajesRepository.save(mensaje);
            }
        }
        // Por alguna razon sin saveAndFlush no actualiza el bool procesada en la bdd
        solicitudRepository.saveAndFlush(solicitud);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Transactional
    public ResponseEntity<?> evaluarEliminacionHecho(SolicitudHechoEvaluarInputDTO dtoInput, Jwt principal) {

        RolCambiadoDTO dto = new RolCambiadoDTO();
        dto.setRolModificado(false);

        ResponseEntity<?> rta = checkeoAdmin(JwtClaimExtractor.getUsernameFromToken(principal));

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
            solicitud.getHecho().getAtributosHecho().setModificado(true);
            solicitud.getHecho().setActivo(false);
            Usuario usuario = usuariosRepository.findById(solicitud.getUsuario_id()).orElse(null);
            // El usuario va a existir si o si porque ya se verificó cuando solicitó eliminar un hecho, pero x si pide borrar la cuenta hago el chequeo antes
            if (usuario != null){
                usuario.disminuirHechosSubidos();
                if (usuario.getCantHechosSubidos() == 0){
                    dto.setRolModificado(true);
                    dto.setRol(Rol.VISUALIZADOR);
                    dto.setUsername(usuario.getNombreDeUsuario());
                    GestorRoles.ContribuyenteAVisualizador(usuario);
                }
            }
        }

        return ResponseEntity.ok().body(dto);
    }

    @Transactional
    public ResponseEntity<?> evaluarModificacionHecho(SolicitudHechoEvaluarInputDTO dtoInput, Jwt principal) {

        ResponseEntity<?> rta = checkeoAdmin(JwtClaimExtractor.getUsernameFromToken(principal));

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
            Usuario usuario = usuariosRepository.findById(solicitud.getUsuario_id()).orElse(null);
            // X si se borró la cuenta del usuario chequeo si es null o no
            if (dtoInput.getMensaje() != null && usuario != null){
                return this.enviarMensaje(usuario,solicitud, dtoInput.getMensaje());
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

        if(atributos.getContenidoMultimediaAgregar() != null){
            hecho.getAtributosHecho().getContenidosMultimedia().addAll(atributos.getContenidoMultimediaAgregar());
        }
        if (atributos.getContenidoMultimediaEliminar() != null){
            hecho.getAtributosHecho().getContenidosMultimedia()
                    .removeIf(contenidoMultimedia ->
                            atributos.getContenidoMultimediaEliminar()
                                    .contains(contenidoMultimedia.getId())
                    );
        }


        Optional.ofNullable(atributos.getLatitud()).ifPresent(hecho.getAtributosHecho()::setLatitud);
        Optional.ofNullable(atributos.getLongitud()).ifPresent(hecho.getAtributosHecho()::setLongitud);
    }

    private ResponseEntity<?> enviarMensaje(Usuario usuario, SolicitudHecho solicitudHecho, String texto){

        Mensaje mensaje = new Mensaje();
        mensaje.setSolicitud_hecho_id(solicitudHecho.getId());
        mensaje.setTextoMensaje(texto);
        mensaje.setReceptor(usuario);
        mensajesRepository.save(mensaje);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getAllSolicitudes(Jwt principal) {

        ResponseEntity<?> rta = checkeoAdmin(JwtClaimExtractor.getUsernameFromToken(principal));

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }


        List<SolicitudHecho> solicitudesHechos = solicitudRepository.findAll();
        List<SolicitudHechoOutputDTO> solicitudHechoOutputDTOS = new ArrayList<>();
        for (SolicitudHecho solicitud: solicitudesHechos){
            SolicitudHechoOutputDTO dto = SolicitudHechoOutputDTO.builder()
                    .id(solicitud.getId())
                    .usuarioId(solicitud.getUsuario_id())
                    .hechoId(solicitud.getHecho().getId())
                    .justificacion(solicitud.getJustificacion())
                    .procesada(solicitud.isProcesada())
                    .rechazadaPorSpam(solicitud.isRechazadaPorSpam())
                    .build();
            solicitudHechoOutputDTOS.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(solicitudHechoOutputDTOS);
    }

    public ResponseEntity<?> obtenerSolicitudesPendientes(String username) {
        ResponseEntity<?> rta = checkeoAdmin(username);

        if (rta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return rta;
        }

        List<SolicitudHecho> solicitudHechos = solicitudRepository.obtenerSolicitudesPendientes();

        List<SolicitudHechoOutputDTO> solicitudHechoOutputDTOS = new ArrayList<>();



        for (SolicitudHecho solicitud: solicitudHechos){
            String nombreUsuario = null;
            if (solicitud.getUsuario_id()!=null){
                Usuario usuario = usuariosRepository.findById(solicitud.getUsuario_id()).orElse(null);
                if (usuario!=null){
                    nombreUsuario = usuario.getNombreDeUsuario();
                }
            }

            SolicitudHechoOutputDTO dto = SolicitudHechoOutputDTO.builder()
                    .id(solicitud.getId())
                    .usuarioId(solicitud.getUsuario_id())
                    .username(nombreUsuario)
                    .hechoId(solicitud.getHecho() != null ? solicitud.getHecho().getId() : null)
                    .justificacion(solicitud.getJustificacion())
                    .procesada(solicitud.isProcesada())
                    .rechazadaPorSpam(solicitud.isRechazadaPorSpam())
                    .tipo(this.clasificacionSolicitud(solicitud))
                    .fecha(solicitud.getFecha() != null ? solicitud.getFecha().toString() : null)
                    .build();
            solicitudHechoOutputDTOS.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(solicitudHechoOutputDTOS);
    }

    @Transactional
    public ResponseEntity<?> reportarHecho(String motivo, Long id_hecho, String fuente) {

        Hecho hecho = null;

        if (fuente.equals(Fuente.DINAMICA.codigoEnString())){
            hecho = hechosDinamicaRepository.findById(id_hecho).orElse(null);
        }
        else if (fuente.equals(Fuente.ESTATICA.codigoEnString())){
            hecho = hechosEstaticaRepository.findById(id_hecho).orElse(null);
        }

        else if (fuente.equals(Fuente.PROXY.codigoEnString())){
            hecho = hechosProxyRepository.findById(id_hecho).orElse(null);
        }

        if(hecho == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el hecho");
        }
        HechoRef hechoRef = new HechoRef(hecho.getId(), hecho.getAtributosHecho().getFuente());
        Reporte reporte = new Reporte(motivo, hechoRef);
        reportesHechoRepository.save(reporte);
        return ResponseEntity.ok().build();
    }

    // Para buscar bien el hecho despues con un boton rápido agrego un endpoint en HechoController para obtener hecho por id y fuente
    public ResponseEntity<?> getAllReportes(Jwt principal) {
        ResponseEntity<?> rta = checkeoAdmin(JwtClaimExtractor.getUsernameFromToken(principal));

        if (!rta.getStatusCode().equals(HttpStatus.OK)) {
            return rta;
        }

        List<Reporte> reportes = reportesHechoRepository.findAllByProcesadoFalse();
        List<ReporteHechoOutputDTO> outputDTOS = new ArrayList<>();

        for (Reporte reporte : reportes){
            ReporteHechoOutputDTO outputDTO = ReporteHechoOutputDTO.builder()
                    .id(reporte.getId())
                    .id_hecho(reporte.getHecho_asociado().getKey().getId())
                    .fuente(reporte.getHecho_asociado().getKey().getFuente().codigoEnString())
                    .motivo(reporte.getMotivo())
                    .build();
            outputDTOS.add(outputDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(outputDTOS);
    }

    @Transactional
    public ResponseEntity<?> evaluarReporte(EvaluarReporteInputDTO inputDTO, Jwt principal) {

        RolCambiadoDTO dto = new RolCambiadoDTO();
        dto.setRolModificado(false);

        ResponseEntity<?> rta = checkeoAdmin(JwtClaimExtractor.getUsernameFromToken(principal));

        if (!rta.getStatusCode().equals(HttpStatus.OK)) {
            return rta;
        }
        // Al pedo pq ya está con jakarta en el dto pero bue
        if (inputDTO.getReporte_id() == null) {
            return ResponseEntity.badRequest().build();
        }

        Reporte reporte = reportesHechoRepository.findById(inputDTO.getReporte_id()).orElse(null);

        if (reporte == null) {
            return ResponseEntity.notFound().build();
        }

        reporte.setProcesado(true);
        if (inputDTO.getRespuesta()) {
            Fuente fuente = reporte.getHecho_asociado().getKey().getFuente();
            Long id = reporte.getHecho_asociado().getKey().getId();
            Hecho hecho = null;
            if (fuente.equals(Fuente.ESTATICA)) {
                hecho = hechosEstaticaRepository.findById(id).orElse(null);
            }
            else if (fuente.equals(Fuente.DINAMICA)) {
                hecho = hechosDinamicaRepository.findById(id).orElse(null);
            }
            else if (fuente.equals(Fuente.PROXY)) {
                hecho = hechosProxyRepository.findById(id).orElse(null);
            }
            if (hecho == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el hecho");
            hecho.setActivo(false);
            hecho.getAtributosHecho().setModificado(true);
            Usuario usuario = usuariosRepository.findById(hecho.getUsuario_id()).orElse(null);
            // x si pide borrar la cuenta hago el chequeo antes
            if (usuario != null) {
                usuario.disminuirHechosSubidos();
                if (usuario.getCantHechosSubidos() == 0) {
                    dto.setRolModificado(true);
                    dto.setRol(Rol.VISUALIZADOR);
                    dto.setUsername(usuario.getNombreDeUsuario());
                    GestorRoles.ContribuyenteAVisualizador(usuario);
                }
            }
        }

        return ResponseEntity.ok().body(dto);

    }

    public ResponseEntity<Integer> getPorcentajeSolicitudesProcesadas() {

        System.out.println("Entre a solicitudes");
        Integer porcentaje = solicitudRepository.porcentajeProcesadas().intValue();
        System.out.println("PORCENTAJE: " + porcentaje);
        return ResponseEntity.ok().body(porcentaje);
    }

    public String clasificacionSolicitud(SolicitudHecho solicitud){
        String tipo = null;

        if (solicitud instanceof SolicitudSubirHecho){
            tipo = "SUBIR";
        }
        else if (solicitud instanceof SolicitudModificarHecho){
            tipo = "MODIFICAR";
        }
        else if (solicitud instanceof SolicitudEliminarHecho){
            tipo = "ELIMINAR";
        }
        return tipo;
    }

}


