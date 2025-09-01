package modulos.agregacion.services;

import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.Filtrador;
import modulos.agregacion.entities.filtros.*;
import modulos.agregacion.repositories.*;
import modulos.agregacion.entities.fuentes.Dataset;
import modulos.agregacion.entities.fuentes.Origen;
import modulos.shared.dtos.input.*;
import modulos.agregacion.entities.RespuestaHttp;
import org.springframework.scheduling.annotation.Async;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.agregacion.entities.fuentes.FuenteEstatica;
import modulos.agregacion.entities.usuario.Rol;
import modulos.agregacion.entities.usuario.Usuario;
import modulos.agregacion.entities.AtributosHecho;
import modulos.shared.dtos.input.ImportacionHechosInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.agregacion.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
public class HechosService {


    private final IHechosProxyRepository hechosProxyRepo;
    private final IHechosEstaticaRepository hechosEstaticaRepo;
    private final IHechosDinamicaRepository hechosDinamicaRepo;
    private final IUsuarioRepository usuariosRepo;
    private final IColeccionRepository coleccionRepo;
    private final IDatasetsRepository datasetsRepo;

    public HechosService(IHechosProxyRepository hechosProxyRepo,
                         IHechosEstaticaRepository hechosEstaticaRepo,
                         IHechosDinamicaRepository hechosDinamicaRepo,
                         IUsuarioRepository usuariosRepo,
                         IColeccionRepository coleccionRepo,
                         IDatasetsRepository datasetsRepo) {
        this.hechosProxyRepo = hechosProxyRepo;
        this.hechosDinamicaRepo = hechosDinamicaRepo;
        this.hechosEstaticaRepo = hechosEstaticaRepo;
        this.usuariosRepo = usuariosRepo;
        this.coleccionRepo = coleccionRepo;
        this.datasetsRepo = datasetsRepo;
    }

    /* Se pide que, una vez por hora, el servicio de agregación actualice los hechos pertenecientes a las distintas colecciones,
     en caso de que las fuentes hayan incorporado nuevos hechos.*/
    @Async
    @Scheduled(cron = "0 0 * * * *")
    public void actualizarColeccionesCronjob(){
        List<Coleccion> colecciones = coleccionRepo.findAll();
        for(Coleccion coleccion: colecciones){
            List<Hecho> hechosFiltrados = Filtrador.aplicarFiltros(coleccion.getCriterios(),coleccion.getHechos());
            coleccion.setHechos(hechosFiltrados);
            coleccionRepo.save(coleccion);
        }
    }


    /*

    Resumen lógico

Si solo cambian hechos → reviso solo esos hechos contra todas las colecciones.

Si solo cambian colecciones → reviso todos los hechos contra esas colecciones.

Si cambian ambos →

Para colecciones modificadas → reviso todos los hechos.

Para colecciones no modificadas → reviso solo los hechos cambiados

    */

    // MODIFICADO -> TRUE SI HAY QUE EVALUARLO
    // MODIFICADO -> FALSE SI NO!!!!

    @Async
    @Scheduled(cron = "0 0 * * * *") // cada hora
    public void refrescarColeccionesCronjob() {

        List<Hecho> hechosEstatica = hechosEstaticaRepo.findAll();
        List<Hecho> hechosDinamica = hechosDinamicaRepo.findAll();

        List<Hecho> hechos = Stream.concat(hechosEstatica.stream(), hechosDinamica.stream())
                .toList();

        List<Coleccion> colecciones = coleccionRepo.findAll();

        List<Hecho> hechosModificados = hechos.stream().filter(
                hecho->hecho.getAtributosHecho().getModificado().equals(true)
                && hecho.getActivo().equals(true))
                .toList();

        List<Coleccion> coleccionesModificadas = colecciones.stream().filter(
                coleccion -> coleccion.getModificado().equals(true)
                && coleccion.getActivo().equals(true))
                .toList();

        if (!hechosModificados.isEmpty()){
            this.eliminarHechosModificadosDeColecciones(colecciones, hechosModificados);
            this.mapearHechosAColecciones(colecciones,hechosModificados);
        }
        if (!coleccionesModificadas.isEmpty()){
            this.mapearHechosAColecciones(coleccionesModificadas, hechos);
        }
        this.setearFalseModificado(hechosModificados,coleccionesModificadas);
    }

    public RespuestaHttp<Void> refrescarColecciones(Long idUsuario){
        Usuario usuario = usuariosRepo.findById(idUsuario).orElse(null);
        if (usuario!= null && !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        this.refrescarColeccionesCronjob();
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    private void eliminarHechosModificadosDeColecciones(List<Coleccion> colecciones, List<Hecho> hechos){

        for (Coleccion coleccion: colecciones){
            List<Hecho> hechosColeccion = coleccion.getHechos();

            for (Hecho hecho: hechos){

                Long hechoId = hecho.getId();
                Origen hechoOrigen = hecho.getAtributosHecho().getOrigen();

                Hecho hechoEncontrado = hechosColeccion.stream()
                                        .filter(h -> h.getId().equals(hechoId) && h.getAtributosHecho().getOrigen().equals(hechoOrigen))
                                        .findFirst().orElse(null);
                if (hechoEncontrado != null && !Filtrador.hechoPasaFiltros(coleccion.getCriterios(), hecho)){
                    hechosColeccion.remove(hecho);
                }
            }
        }

    }

    public void setearFalseModificado(List<Hecho> hechos, List<Coleccion> colecciones){
        for (Hecho hecho : hechos){
            hecho.getAtributosHecho().setModificado(false);
        }
        for (Coleccion coleccion : colecciones){
        coleccion.setModificado(false);
        }
    }

    public void mapearHechosAColecciones(List<Coleccion> colecciones, List<Hecho> hechos){

        for (Coleccion coleccion : colecciones){
            List<Hecho> hechosFiltrados = Filtrador.aplicarFiltros(coleccion.getCriterios(), hechos);
            for (Hecho hecho : hechosFiltrados) {
                if (!coleccion.getHechos().contains(hecho)){
                    coleccion.addHechos(hecho);
                }
            }

        }

    }

    public void mapearHechoAColecciones(Hecho hecho){
        List<Coleccion> colecciones = coleccionRepo.findAll();

        for (Coleccion coleccion : colecciones){

            boolean cumpleCriterio = Filtrador.hechoPasaFiltros(coleccion.getCriterios(), hecho);

            if (cumpleCriterio){
                coleccion.addHechos(hecho);
            }

        }
    }
    //lo sube un administrador (lo considero carga dinamica)
    public RespuestaHttp<Void> subirHecho(SolicitudHechoInputDTO dtoInput) {

        FormateadorHecho formateador = new FormateadorHecho();

        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario()).orElse(null);
        if(usuario.getRol().equals(Rol.ADMINISTRADOR)){

            Hecho hecho = new Hecho();

            AtributosHecho atributos = formateador.formatearAtributosHecho(hechosDinamicaRepo.findAll(),hechosEstaticaRepo.findAll(),hechosProxyRepo.findAll(),dtoInput);

            hecho.setAtributosHecho(atributos);
            hecho.setActivo(true);
            hecho.getAtributosHecho().setFechaCarga(ZonedDateTime.now());
            hecho.getAtributosHecho().setFechaUltimaActualizacion(hecho.getAtributosHecho().getFechaCarga());
            hechosDinamicaRepo.save(hecho);

            return new RespuestaHttp<>(null, HttpStatus.CREATED.value());
        }
        else{
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
    }

    public RespuestaHttp<Void> importarHechos(ImportacionHechosInputDTO dtoInput){
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario()).orElse(null);
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){

            FuenteEstatica fuente = new FuenteEstatica();
            Dataset dataset = new Dataset(dtoInput.getFuenteString());
            datasetsRepo.save(dataset);
            fuente.setDataSet(dataset);

            List<Hecho> hechos = fuente.leerFuente(hechosDinamicaRepo.findAll(),hechosProxyRepo.findAll(),hechosEstaticaRepo.findAll());


            if (hechos.isEmpty()){
                return new RespuestaHttp<>(null, HttpStatus.NO_CONTENT.value());
            }

            for (Hecho hecho : hechos){
                hechosEstaticaRepo.save(hecho);
            }

            return new RespuestaHttp<>(null, HttpStatus.CREATED.value());
        }

        return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());

    }

    public RespuestaHttp<List<VisualizarHechosOutputDTO>> getHechosColeccion(GetHechosColeccionInputDTO inputDTO){

        Map<Class<? extends Filtro>, Filtro> filtros;

        FormateadorHecho formateador = new FormateadorHecho();
        filtros = formateador.obtenerMapaDeFiltros(formateador.formatearFiltrosColeccion(hechosDinamicaRepo.findAll(),hechosEstaticaRepo.findAll(),hechosProxyRepo.findAll(),new CriteriosColeccionDTO(
                inputDTO.getCategoria(),
                inputDTO.getContenidoMultimedia(),
                inputDTO.getDescripcion(),
                inputDTO.getFechaAcontecimientoInicial(),
                inputDTO.getFechaAcontecimientoFinal(),
                inputDTO.getFechaCargaInicial(),
                inputDTO.getFechaCargaFinal(),
                inputDTO.getOrigen(),
                inputDTO.getPais(),
                inputDTO.getTitulo()
        )));

        Coleccion coleccion = coleccionRepo.findById(inputDTO.getId_coleccion()).orElse(null);

        if (coleccion == null){
            return new RespuestaHttp<>(null, HttpStatus.NO_CONTENT.value());
        }

        List<Hecho> hechosColeccion;

        if (inputDTO.getNavegacionCurada()){
            hechosColeccion = new ArrayList<>(coleccion.getHechosConsensuados());
        }
        else{
            hechosColeccion = coleccion.getHechos();
        }

        List<Hecho> hechosFiltrados = Filtrador
                .aplicarFiltros(filtros, hechosColeccion);

        List<VisualizarHechosOutputDTO> outputDTO = hechosFiltrados.stream().map(hecho -> {
            VisualizarHechosOutputDTO dto = new VisualizarHechosOutputDTO();
            dto.setId(hecho.getId());
            dto.setPais(hecho.getAtributosHecho().getUbicacion().getPais().getPais());
            dto.setProvincia(hecho.getAtributosHecho().getUbicacion().getProvincia().getProvincia());
            dto.setTitulo(hecho.getAtributosHecho().getTitulo());
            dto.setDescripcion(hecho.getAtributosHecho().getDescripcion());
            dto.setFechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento().toString());
            dto.setCategoria(hecho.getAtributosHecho().getCategoria().getTitulo());
            return dto;
        }).toList();

        return new RespuestaHttp<>(outputDTO, HttpStatus.OK.value());
    }

    public RespuestaHttp<List<VisualizarHechosOutputDTO>> getAllHechos() {
        List<Hecho> hechosTotales = new ArrayList<>();
        hechosTotales.addAll(hechosDinamicaRepo.findAll());
        hechosTotales.addAll(hechosProxyRepo.findAll());
        hechosTotales.addAll(hechosEstaticaRepo.findAll());

        List<VisualizarHechosOutputDTO> outputDTO = hechosTotales.stream().map(hecho -> {
            VisualizarHechosOutputDTO dto = new VisualizarHechosOutputDTO();
            dto.setId(hecho.getId());
            dto.setPais(hecho.getAtributosHecho().getUbicacion().getPais().getPais());
            dto.setProvincia(hecho.getAtributosHecho().getUbicacion().getProvincia().getProvincia());
            dto.setTitulo(hecho.getAtributosHecho().getTitulo());
            dto.setDescripcion(hecho.getAtributosHecho().getDescripcion());
            dto.setFechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento().toString());
            dto.setCategoria(hecho.getAtributosHecho().getCategoria().getTitulo());
            return dto;
        }).toList();

        return new RespuestaHttp<>(outputDTO,HttpStatus.OK.value());
    }

}
