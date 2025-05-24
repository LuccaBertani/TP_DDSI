package services.impl;

import models.dtos.input.FiltroHechosDTO;
import models.dtos.input.ImportacionHechosInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.dtos.input.VisualizarHechosInputDTO;
import models.dtos.output.VisualizarHechosOutputDTO;
import models.entities.*;
import models.entities.buscadores.BuscadorCategoria;
import models.entities.buscadores.BuscadorPais;
import models.entities.casosDeUso.NavegarPorHechos;
import models.entities.filtros.*;
import models.entities.fuentes.Fuente;
import models.entities.fuentes.FuenteEstatica;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IColeccionRepository;
import models.repositories.IHechosRepository;
import models.repositories.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import services.IColeccionService;
import services.IHechosService;

import java.sql.Array;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HechosService implements IHechosService {


    private final IHechosRepository hechosRepo;
    private final IPersonaRepository usuariosRepo;
    private final IColeccionRepository coleccionRepo;

    private List<Hecho> snapshotHechos;
    private Boolean hechosNuevosSubidos;

    public HechosService(IHechosRepository repo, IPersonaRepository usuariosRepo, IColeccionRepository coleccionRepo) {
        this.hechosRepo = repo;
        this.usuariosRepo = usuariosRepo;
        this.coleccionRepo = coleccionRepo;
        snapshotHechos = new ArrayList<>();
        hechosNuevosSubidos = false;
    }

    // Hay que controlar condiciones de carrera entre el chequeo de nuevos hechos y la actualizacion de la snapshot
    @Scheduled(cron = "0 */20 * * * *") // cada 20 minutos
    public void chequearNuevosHechos(){
        List<Hecho> todosHechos = hechosRepo.findAll()
                .stream()
                .filter(hecho -> !this.seEncuentraEnColeccionProxyMetaMapa(hecho))
                .toList();

        // Si la cantidad de hechos totales es mayor a la cantidad de hechos en la snapshot, significa que hay nuevos hechos subidos
        if (todosHechos.size() > snapshotHechos.size()){
            this.hechosNuevosSubidos = true;
        }
    }
    /* Se pide que, una vez por hora, el servicio de agregación actualice los hechos pertenecientes a las distintas colecciones,
     en caso de que las fuentes hayan incorporado nuevos hechos.*/
    @Scheduled(cron = "0 0 * * * *") // cada hora
    public void refrescarSnapshot() {
        if (this.hechosNuevosSubidos){
            List<Hecho> hechosTotales = hechosRepo.findAll()
                    .stream()
                    .filter(hecho -> !this.seEncuentraEnColeccionProxyMetaMapa(hecho))
                    .toList();

            // Cómo sabemos a qué colección van a parar los hechos?
            // this.snapshotHechos = ???

            this.hechosNuevosSubidos = false;
        }
    }

    /*Se excluye de este requerimiento la actualización de los hechos provenientes de fuentes proxy MetaMapa, que deben ser obtenidos
    en tiempo real en todos los casos.*/
    public Boolean seEncuentraEnColeccionProxyMetaMapa(Hecho hecho){
        List<Coleccion> colecciones = coleccionRepo.findAll();
        List<Coleccion> coleccionesProxy = colecciones.stream().filter(coleccion->coleccion.getFuente().equals("ProxyMetaMapa")).toList();
        return coleccionesProxy.stream().anyMatch(c->c.getHechos().contains(hecho));
    }

    public List<Hecho> getSnapshot() {
        return this.snapshotHechos;
    }

    @Override
    public RespuestaHttp<Void> subirHecho(SolicitudHechoInputDTO dtoInput) {
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if(usuario.getRol().equals(Rol.ADMINISTRADOR)){

            Hecho hecho = new Hecho();

            List<Hecho> hechos = hechosRepo.findAll(); // TODO cambiar x la temporal

            if(dtoInput.getPais() != null) {
                Pais pais = BuscadorPais.buscar(hechosRepo.findAll(),dtoInput.getPais());
                hecho.setPais(pais);
            }else{
                Pais pais = BuscadorPais.buscar(hechosRepo.findAll(),"N/A");
                hecho.setPais(pais);
            }

            hecho.setTitulo(dtoInput.getTitulo());
            if(dtoInput.getDescripcion() != null) {
                hecho.setDescripcion(dtoInput.getDescripcion());
            }else{
                hecho.setDescripcion("N/A");
            }
            if(dtoInput.getFechaAcontecimiento() != null) {
                hecho.setFechaAcontecimiento(FechaParser.parsearFecha(dtoInput.getFechaAcontecimiento()));
            }else{
                hecho.setFechaAcontecimiento(ZonedDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")));
            }
            if(dtoInput.getTipoContenido() != null){
                hecho.setContenidoMultimedia(TipoContenido.fromCodigo(dtoInput.getTipoContenido()));
            }else{
                hecho.setContenidoMultimedia(TipoContenido.INVALIDO);
            }
            if(dtoInput.getCategoria() != null){
                Categoria categoria = BuscadorCategoria.buscar(hechosRepo.findAll(),dtoInput.getCategoria());
                hecho.setCategoria(categoria);
            }
            else{
                Categoria categoria = BuscadorCategoria.buscar(hechosRepo.findAll(),"N/A");
                hecho.setCategoria(categoria);
            }
            hecho.setOrigen(Origen.CARGA_MANUAL);
            hecho.setId(hechosRepo.getProxId());
            hecho.setActivo(true);
            hecho.setFechaDeCarga(ZonedDateTime.now());
            hechosRepo.save(hecho);
            return new RespuestaHttp<>(null, HttpStatus.OK.value());
        }
        else{
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    public RespuestaHttp<Void> importarHechos(ImportacionHechosInputDTO dtoInput){
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){
            // Se borran y suben hechos constantemente => Guardamos los que se tienen hasta el momento en una lista
            // TODO SNAPSHOT
            // En vez de usar el find all, en este caso como la fuente es estatica, se usaria la snapshot
            List<Hecho> hechosActuales = hechosRepo.findAll();



            FuenteEstatica fuente = new FuenteEstatica();
            fuente.setDataSet(dtoInput.getFuenteString());
            ModificadorHechos modificadorHechos = fuente.leerFuente(hechosActuales);

            List<Hecho> hechosASubir = modificadorHechos.getHechosASubir();
            List<Hecho> hechosAModificar = modificadorHechos.getHechosAModificar();

            for (Hecho hecho : hechosASubir){
                hecho.setId(hechosRepo.getProxId());
                hechosRepo.save(hecho);
            }
            for (Hecho hecho : hechosAModificar){
                hechosRepo.update(hecho);
            }
            return new RespuestaHttp<>(null, HttpStatus.OK.value());
        }

        return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());

    }

    @Override
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(FiltroHechosDTO inputDTO) {

        List<Filtro> filter = new ArrayList<>();

        if (inputDTO.getCategoria() != null) {
            filter.add(new FiltroCategoria(BuscadorCategoria.buscar(hechosRepo.findAll(), inputDTO.getCategoria())));
        }

        if (inputDTO.getContenidoMultimedia() != null) {
            TipoContenido contenido = TipoContenido.fromCodigo(Integer.parseInt(inputDTO.getContenidoMultimedia()));
            filter.add(new FiltroContenidoMultimedia(contenido));
        }

        if (inputDTO.getDescripcion() != null) {
            filter.add(new FiltroDescripcion(inputDTO.getDescripcion()));
        }

        if (inputDTO.getFechaAcontecimientoInicial() != null && inputDTO.getFechaAcontecimientoFinal() != null) {
            ZonedDateTime inicio = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoInicial());
            ZonedDateTime fin = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoFinal());
            filter.add(new FiltroFechaAcontecimiento(inicio, fin));
        }

        if (inputDTO.getFechaCargaInicial() != null && inputDTO.getFechaCargaFinal() != null) {
            ZonedDateTime inicio = FechaParser.parsearFecha(inputDTO.getFechaCargaInicial());
            ZonedDateTime fin = FechaParser.parsearFecha(inputDTO.getFechaCargaFinal());
            filter.add(new FiltroFechaCarga(inicio, fin));
        }

        if (inputDTO.getOrigen() != null) {
            Origen origen = Origen.fromCodigo(Integer.parseInt(inputDTO.getOrigen()));
            filter.add(new FiltroOrigen(origen));
        }

        if (inputDTO.getPais() != null) {
            filter.add(new FiltroPais(BuscadorPais.buscar(hechosRepo.findAll(), inputDTO.getPais())));
        }

        if (inputDTO.getTitulo() != null) {
            filter.add(new FiltroTitulo(inputDTO.getTitulo()));
        }

        List<Hecho> hechosFiltrados = new Filtrador()
                .aplicarFiltros(filter, coleccionRepo.findById(inputDTO.getId_coleccion()).getHechos());

        List<VisualizarHechosOutputDTO> outputDTO = hechosFiltrados.stream().map(hecho -> {
            VisualizarHechosOutputDTO dto = new VisualizarHechosOutputDTO();
            dto.setId(hecho.getId());
            dto.setPais(hecho.getPais().getPais());
            dto.setTitulo(hecho.getTitulo());
            dto.setDescripcion(hecho.getDescripcion());
            dto.setFechaAcontecimiento(hecho.getFechaAcontecimiento().toString());
            dto.setCategoria(hecho.getCategoria().getTitulo());
            return dto;
        }).toList();

        return new RespuestaHttp<>(outputDTO, HttpStatus.OK.value());
    }

    @Override
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(Long id_coleccion){

        Coleccion coleccion = coleccionRepo.findById(id_coleccion);

        List<VisualizarHechosOutputDTO> outputDTO = new ArrayList<>();

        for(Hecho hecho : coleccion.getHechos()){

            VisualizarHechosOutputDTO hechoDTO = new VisualizarHechosOutputDTO();

            hechoDTO.setId(hecho.getId());
            hechoDTO.setPais(hecho.getPais().getPais());
            hechoDTO.setTitulo(hecho.getTitulo());
            hechoDTO.setDescripcion(hecho.getDescripcion());
            hechoDTO.setFechaAcontecimiento(hecho.getFechaAcontecimiento().toString());
            hechoDTO.setCategoria(hecho.getCategoria().getTitulo());

            outputDTO.add(hechoDTO);

        }

        return new RespuestaHttp<>(outputDTO,HttpStatus.OK.value());

    }




}
