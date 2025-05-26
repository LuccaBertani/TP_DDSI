package modulos.agregacion;

import models.dtos.input.*;
import models.entities.*;
import models.entities.buscadores.BuscadorCategoria;
import models.entities.buscadores.BuscadorPais;
import models.entities.filtros.*;
import models.entities.fuentes.FuenteDinamica;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*
@Service
public class ServiceAgregacion {

    private final IHechosRepository hechosRepo;
    private final IColeccionRepository coleccionesRepo;
    private final IPersonaRepository usuariosRepo;
    private final IPersonaRepository personasRepo;
    private final ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo;
    private final ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo;
    private final ISolicitudModificarHechoRepository solicitudModificarHechoRepo;
    GestorRoles gestorRoles;

    public ServiceAgregacion(IHechosRepository hechosRepo, IColeccionRepository coleccionesRepo, IPersonaRepository usuariosRepo, IPersonaRepository personasRepo, ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo, ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo, ISolicitudModificarHechoRepository solicitudModificarHechoRepo) {
        this.hechosRepo = hechosRepo;
        this.coleccionesRepo = coleccionesRepo;
        this.usuariosRepo = usuariosRepo;
        this.personasRepo = personasRepo;
        this.solicitudAgregarHechoRepo = solicitudAgregarHechoRepo;
        this.solicitudEliminarHechoRepo = solicitudEliminarHechoRepo;
        this.solicitudModificarHechoRepo = solicitudModificarHechoRepo;
        gestorRoles = new GestorRoles();
    }

    public RespuestaHttp<Void> crearColeccion(ColeccionInputDTO dtoInput) {

        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        DatosColeccion datosColeccion = new DatosColeccion(dtoInput.getTitulo(), dtoInput.getDescripcion(), dtoInput.getFuente());

        Coleccion coleccion = new Coleccion(datosColeccion,coleccionesRepo.getProxId());

        List<Filtro> criterios = new ArrayList<>();


        if(dtoInput.getPais() != null) {
            Pais pais = BuscadorPais.buscar(hechosRepo.findAll(),dtoInput.getPais());
            Filtro filtroPais = new FiltroPais(pais);
            criterios.add(filtroPais);
        }
        if(dtoInput.getFechaAcontecimientoFinal() != null && dtoInput.getFechaAcontecimientoInicial() != null) {
            Filtro filtroFechaAcontecimiento = new FiltroFechaAcontecimiento(FechaParser.parsearFecha(dtoInput.getFechaAcontecimientoInicial()),FechaParser.parsearFecha(dtoInput.getFechaAcontecimientoFinal()));
            criterios.add(filtroFechaAcontecimiento);
        }
        if(dtoInput.getContenidoMultimedia() != null) {
            Filtro filtroContenidoMultimedia = new FiltroContenidoMultimedia(TipoContenido.fromCodigo(dtoInput.getContenidoMultimedia()));
            criterios.add(filtroContenidoMultimedia);
        }
        if(dtoInput.getCategoria() != null){
            Categoria categoria = BuscadorCategoria.buscar(hechosRepo.findAll(),dtoInput.getCategoria());
            Filtro filtroCategoria = new FiltroCategoria(categoria);
            criterios.add(filtroCategoria);
        }
        if (dtoInput.getFechaCargaInicial()!=null && dtoInput.getFechaCargaFinal() !=null){
            Filtro filtroFechaCarga = new FiltroFechaAcontecimiento(FechaParser.parsearFecha(dtoInput.getFechaCargaInicial()), FechaParser.parsearFecha(dtoInput.getFechaCargaFinal()));
        }

        coleccion.addCriterios(criterios);
        List<Hecho> hechos = hechosRepo.findAll();

        coleccion.addHechos(Filtrador.aplicarFiltros(criterios, hechos));

        coleccionesRepo.save(coleccion);

        return new RespuestaHttp<>(null, HttpStatus.OK.value());

    }

    public RespuestaHttp<List<Usuario>> obtenerListaContribuyentes(Long id_usuario) {
        Usuario usuario = personasRepo.findById(id_usuario);
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)) {
            List<Usuario> usuarios = personasRepo.findAll();
            return new RespuestaHttp<>(usuarios, HttpStatus.OK.value());
        }
        return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
    }

    public RespuestaHttp<Void> solicitarSubirHecho(SolicitudHechoInputDTO dto) {

        Usuario usuario = usuariosRepo.findById(dto.getId_usuario());

        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        List<Hecho> hechos = hechosRepo.findAll();

        Optional<Hecho> hecho2 = hechos.stream().filter(h->Normalizador.normalizarYComparar(h.getPais().getPais(), dto.getPais())).findFirst();
        Pais pais;

        if (hecho2.isPresent()){
            pais = hecho2.get().getPais();
        } else {
            pais = new Pais();
            pais.setPais(dto.getPais());
        }

        HechosData hechosData = new HechosData(dto.getTitulo(), dto.getDescripcion(), dto.getTipoContenido(),
                pais, dto.getFechaAcontecimiento(), hechosRepo.getProxId());

        FuenteDinamica fuenteDinamica = new FuenteDinamica();
        Hecho hecho = fuenteDinamica.crearHecho(hechosData);
        SolicitudHecho solicitudHecho = new SolicitudHecho(usuario, hecho, solicitudAgregarHechoRepo.getProxId());
        solicitudAgregarHechoRepo.save(solicitudHecho);
        hecho.setId_usuario(usuario.getId());
        hechosRepo.save(hecho);

        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    //El usuario manda una solicitud para eliminar un hecho -> guardar la solicitud en la base de datos
    public RespuestaHttp<Void> solicitarEliminacionHecho(SolicitudHechoEliminarInputDTO dto){
        Usuario usuario = usuariosRepo.findById(dto.getId_usuario());
        if (usuario == null || usuario.getRol().equals(Rol.ADMINISTRADOR) || usuario.getRol().equals(Rol.VISUALIZADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        Hecho hecho = hechosRepo.findById(dto.getId_hecho());
        SolicitudHecho solicitud = new SolicitudHecho(usuario, hecho, solicitudEliminarHechoRepo.getProxId());
        solicitudEliminarHechoRepo.save(solicitud);
        return new RespuestaHttp<>(null, HttpStatus.OK.value()); // Un admin no deberia solicitar eliminar, los elimina directamente
    }

    public RespuestaHttp<Void> solicitarModificacionHecho(SolicitudHechoModificarInputDTO dto){

        Usuario usuario = usuariosRepo.findById(dto.getId_usuario());

        if (usuario == null || usuario.getId().equals(hechosRepo.findById(dto.getId_hecho()).getId_usuario()) || usuario.getRol().equals(Rol.ADMINISTRADOR) || usuario.getRol().equals(Rol.VISUALIZADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        Hecho hecho = hechosRepo.findById(dto.getId_hecho());

        hecho.setTitulo(dto.getTitulo());
        hecho.setPais(BuscadorPais.buscar(hechosRepo.findAll(), dto.getPais()));
        hecho.setCategoria(BuscadorCategoria.buscar(hechosRepo.findAll(), dto.getPais()));
        hecho.setTitulo(dto.getTitulo());
        hecho.setFechaAcontecimiento(FechaParser.parsearFecha(dto.getFechaAcontecimiento()));
        hecho.setFechaDeCarga(ZonedDateTime.now()); // Nueva fecha de modificación
        hecho.setContenidoMultimedia(TipoContenido.fromCodigo(dto.getTipoContenido()));

        SolicitudHecho solicitud = new SolicitudHecho(usuario, hecho, solicitudModificarHechoRepo.getProxId());
        solicitudModificarHechoRepo.save(solicitud);
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    public RespuestaHttp<Void> evaluarSolicitudSubirHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudAgregarHechoRepo.findById(dtoInput.getId_solicitud());
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());//el que ejecuta la acción

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        else {

            if (dtoInput.getRespuesta()) {

                solicitud.getHecho().setActivo(true);
                solicitud.getUsuario().incrementarHechosSubidos();
                hechosRepo.getSnapshotHechos().add(solicitud.getHecho());
                hechosRepo.update(solicitud.getHecho());

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
        if (ChronoUnit.DAYS.between(solicitud.getHecho().getFechaDeCarga(), ZonedDateTime.now()) >= 7){
            return new RespuestaHttp<>(null, HttpStatus.CONFLICT.value()); // Error 409: cuando la solicitud es válida pero no puede procesarse por estado actual del recurso
        }
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());//el que ejecuta la acción

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        else {
            if (dtoInput.getRespuesta()) {


                solicitud.getUsuario().disminuirHechosSubidos();
                hechosRepo.update(solicitud.getHecho());

                if (solicitud.getUsuario().getCantHechosSubidos() == 0){
                    gestorRoles.ContribuyenteAVisualizador(solicitud.getUsuario());
                }
            }

        }
        solicitudEliminarHechoRepo.delete(solicitud);
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    public RespuestaHttp<Void> evaluarModificacionHecho(SolicitudHechoEvaluarInputDTO dtoInput) {

        SolicitudHecho solicitud = solicitudModificarHechoRepo.findById(dtoInput.getId_solicitud());
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());//el que ejecuta la acción

        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        else {
            if (dtoInput.getRespuesta()) {
                // El hecho debe modificarse
                hechosRepo.getSnapshotHechos().add(solicitud.getHecho());
                hechosRepo.update(solicitud.getHecho());
            }
        }
        solicitudModificarHechoRepo.delete(solicitud);
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

}
*/