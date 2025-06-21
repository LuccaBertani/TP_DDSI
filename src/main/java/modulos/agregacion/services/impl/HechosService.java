package modulos.agregacion.services.impl;

import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.Filtrador;
import modulos.agregacion.entities.ModificadorHechos;
import modulos.agregacion.entities.filtros.*;
import modulos.agregacion.repositories.*;
import modulos.fuentes.Dataset;
import modulos.fuentes.Origen;
import modulos.shared.*;
import modulos.shared.dtos.input.GetHechosColeccionInputDTO;
import modulos.shared.utils.FechaParser;
import org.springframework.scheduling.annotation.Async;
import modulos.shared.dtos.input.FiltroHechosDTO;
import modulos.shared.dtos.input.ImportacionHechosInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorPais;
import modulos.fuentes.FuenteEstatica;
import modulos.usuario.Rol;
import modulos.usuario.Usuario;
import modulos.shared.dtos.AtributosHecho;
import modulos.shared.dtos.input.FiltroHechosDTO;
import modulos.shared.dtos.input.ImportacionHechosInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.agregacion.entities.*;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorPais;
import modulos.agregacion.entities.filtros.*;
import modulos.fuentes.FuenteEstatica;
import modulos.usuario.Rol;
import modulos.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class HechosService {


    private final IHechosProxyRepository hechosProxyRepo;
    private final IHechosDinamicaRepository hechosDinamicaRepo;
    private final IHechosEstaticaRepository hechosEstaticaRepo;
    private final IUsuarioRepository usuariosRepo;
    private final IColeccionRepository coleccionRepo;

    private final Lock mutexRefrescarColecciones = new ReentrantLock();

    public HechosService(IHechosProxyRepository repo, IHechosProxyRepository hechosProxyRepo, IHechosDinamicaRepository hechosDinamicaRepo, IHechosEstaticaRepository hechosEstaticaRepo, IUsuarioRepository usuariosRepo, IColeccionRepository coleccionRepo) {
        this.hechosProxyRepo = hechosProxyRepo;
        this.hechosDinamicaRepo = hechosDinamicaRepo;
        this.hechosEstaticaRepo = hechosEstaticaRepo;
        this.usuariosRepo = usuariosRepo;
        this.coleccionRepo = coleccionRepo;
    }

    /* Se pide que, una vez por hora, el servicio de agregación actualice los hechos pertenecientes a las distintas colecciones,
     en caso de que las fuentes hayan incorporado nuevos hechos.*/

    // TODO sincronizar los metodos?

    @Async
    @Scheduled(cron = "0 0 * * * *")
    public void actualizarColeccionesCronjob(){
        List<Coleccion> colecciones = coleccionRepo.findAll();
        for(Coleccion coleccion: colecciones){
            List<Hecho> hechosFiltrados = Filtrador.aplicarFiltros(coleccion.getCriterios(),coleccion.getHechos());
            coleccion.setHechos(hechosFiltrados);
            coleccionRepo.update(coleccion);
        }
    }


    @Async
    @Scheduled(cron = "0 0 * * * *") // cada hora
    public void refrescarColeccionesCronjob() {

        List<Hecho> snapshotHechosEstatica = hechosEstaticaRepo.getSnapshotHechos();
        List<Hecho> snapshotHechosDinamica = hechosDinamicaRepo.getSnapshotHechos();
        List<Coleccion> colecciones = coleccionRepo.findAll();
        if (!snapshotHechosEstatica.isEmpty()){
            this.eliminarHechosModificadosDeColecciones(colecciones, snapshotHechosEstatica);
            this.mapearHechosAColecciones(snapshotHechosEstatica);
            hechosEstaticaRepo.clearSnapshotHechos();
        }
        if(!snapshotHechosDinamica.isEmpty()){
            this.eliminarHechosModificadosDeColecciones(colecciones, snapshotHechosDinamica);
            this.mapearHechosAColecciones(snapshotHechosDinamica);
            hechosDinamicaRepo.clearSnapshotHechos();
        }

    }

    public RespuestaHttp<Void> refrescarColecciones(Long idUsuario){
        Usuario usuario = usuariosRepo.findById(idUsuario);
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
                Origen hechoOrigen = hecho.getOrigen();

                Hecho hechoEncontrado = hechosColeccion.stream()
                                        .filter(h -> h.getId().equals(hechoId) && h.getOrigen().equals(hechoOrigen))
                                        .findFirst().orElse(null);
                if (hechoEncontrado != null && !Filtrador.hechoPasaFiltros(coleccion.getCriterios(), hecho)){
                    hechosColeccion.remove(hecho);
                }
            }
        }

    }


    public void mapearHechosAColecciones(List<Hecho> hechos){

        List<Coleccion> colecciones = coleccionRepo.findAll();

        for (Coleccion coleccion : colecciones){
            List<Hecho> hechosFiltrados = Filtrador.aplicarFiltros(coleccion.getCriterios(), hechos);
            coleccion.addHechos(hechosFiltrados);
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

        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if(usuario.getRol().equals(Rol.ADMINISTRADOR)){

            Hecho hecho = new Hecho();

            AtributosHecho atributos = formateador.formatearAtributosHecho(hechosDinamicaRepo.findAll(),hechosEstaticaRepo.findAll(),hechosProxyRepo.findAll(),dtoInput);

            hecho.setPais(atributos.getPais());
            hecho.setCategoria(atributos.getCategoria());
            hecho.setFechaAcontecimiento(atributos.getFechaAcontecimiento());
            hecho.setDescripcion(atributos.getDescripcion());
            hecho.setOrigen(atributos.getOrigen());
            hecho.setContenidoMultimedia(atributos.getContenidoMultimedia());

            hecho.setId(hechosDinamicaRepo.getProxId());
            hecho.setActivo(true);
            hecho.setFechaDeCarga(ZonedDateTime.now());
            hecho.setFechaUltimaActualizacion(hecho.getFechaDeCarga());
            hechosDinamicaRepo.getSnapshotHechos().add(hecho);
            hechosDinamicaRepo.save(hecho);

            return new RespuestaHttp<>(null, HttpStatus.CREATED.value());
        }
        else{
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
    }

    public RespuestaHttp<Void> importarHechos(ImportacionHechosInputDTO dtoInput){
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){

            FuenteEstatica fuente = new FuenteEstatica();
            fuente.setDataSet(dtoInput.getFuenteString());

            ModificadorHechos modificadorHechos = fuente.leerFuente(hechosDinamicaRepo.findAll(),hechosProxyRepo.findAll(),hechosEstaticaRepo.findAll());

            List<Hecho> hechosASubir = modificadorHechos.getHechosASubir();
            Set<Hecho> hechosAModificar = modificadorHechos.getHechosAModificar();


            if (hechosASubir.isEmpty() && hechosAModificar.isEmpty()){
                return new RespuestaHttp<>(null, HttpStatus.NO_CONTENT.value());
            }

            Dataset dataset = new Dataset();
            dataset.setFuente(dtoInput.getFuenteString());
            dataset.setId(hechosEstaticaRepo.getProxIdDataset());

            hechosEstaticaRepo.saveDataset(dataset);

            for (Hecho hecho : hechosASubir){
                hecho.setId(hechosEstaticaRepo.getProxId());
                hecho.getDatasets().add(dataset);
                hechosEstaticaRepo.getSnapshotHechos().add(hecho);
                hechosEstaticaRepo.save(hecho);
            }

            for (Hecho hecho : hechosAModificar){
                hecho.getDatasets().add(dataset); // Se agregan los datasets de los hechos identicos
            }
            return new RespuestaHttp<>(null, HttpStatus.CREATED.value());
        }

        return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());

    }

    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(FiltroHechosDTO inputDTO) {

        FormateadorHecho formateador = new FormateadorHecho();

        FiltrosColeccion filtros =  formateador.formatearFiltrosColeccion(hechosDinamicaRepo.findAll(),hechosEstaticaRepo.findAll(),hechosProxyRepo.findAll(),inputDTO.getCriterios());

        List<Hecho> hechosFiltrados = Filtrador
                .aplicarFiltros(formateador.obtenerMapaDeFiltros(filtros), this.getAllHechos());

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

    public RespuestaHttp<List<VisualizarHechosOutputDTO>> getHechosColeccion(GetHechosColeccionInputDTO inputDTO){
        List<Filtro> filter = new ArrayList<>();

        if (inputDTO.getCategoria() != null) {
            Categoria categoria = BuscadorCategoria.buscar(hechosDinamicaRepo.findAll(), inputDTO.getCategoria(), hechosProxyRepo.findAll(), hechosEstaticaRepo.findAll());
            if (categoria!=null)
                filter.add(new FiltroCategoria(categoria));
        }

        if (inputDTO.getContenidoMultimedia() != null) {
            TipoContenido contenido = TipoContenido.fromCodigo(Integer.parseInt(inputDTO.getContenidoMultimedia()));
            filter.add(new FiltroContenidoMultimedia(contenido));
        }

        if (inputDTO.getDescripcion() != null) {
            filter.add(new FiltroDescripcion(inputDTO.getDescripcion()));
        }

        ZonedDateTime fechaAcontecimientoInicial = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoInicial());
        ZonedDateTime fechaAcontecimientoFinal = FechaParser.parsearFecha(inputDTO.getFechaAcontecimientoFinal());
        if (fechaAcontecimientoInicial != null && fechaAcontecimientoFinal != null) {
            filter.add(new FiltroFechaAcontecimiento(fechaAcontecimientoInicial, fechaAcontecimientoFinal));
        }

        ZonedDateTime fechaCargaInicial = FechaParser.parsearFecha(inputDTO.getFechaCargaInicial());
        ZonedDateTime fechaCargaFinal = FechaParser.parsearFecha(inputDTO.getFechaCargaFinal());
        if (inputDTO.getFechaCargaInicial() != null && inputDTO.getFechaCargaFinal() != null) {
            filter.add(new FiltroFechaCarga(fechaCargaInicial, fechaCargaFinal));
        }

        if (inputDTO.getOrigen() != null) {
            Origen origen = Origen.fromCodigo(Integer.parseInt(inputDTO.getOrigen()));
            filter.add(new FiltroOrigen(origen));
        }

        String paisString = inputDTO.getPais();
        if (paisString != null) {
            Pais pais = BuscadorPais.buscar(hechosDinamicaRepo.findAll(), paisString, hechosProxyRepo.findAll(), hechosEstaticaRepo.findAll());
            if (pais!=null)
                filter.add(new FiltroPais(pais));
        }

        if (inputDTO.getTitulo() != null) {
            filter.add(new FiltroTitulo(inputDTO.getTitulo()));
        }

        Coleccion coleccion = coleccionRepo.findById(inputDTO.getId_coleccion());

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
                .aplicarFiltros(filter, hechosColeccion);

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


    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechosProxyMetamapa(){

        List<Hecho> hechos = this.getAllHechos();
        List<Hecho> hechosProxyMetamapa = hechos.stream().filter(hecho->hecho.getOrigen().equals(Origen.FUENTE_PROXY_METAMAPA)).toList();
        List<VisualizarHechosOutputDTO> outputDTO = new ArrayList<>();
        for (Hecho hecho : hechosProxyMetamapa){
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

    public List<Hecho> getAllHechos(){
        List<Hecho> hechosTotales = new ArrayList<>();
        hechosTotales.addAll(hechosDinamicaRepo.findAll());
        hechosTotales.addAll(hechosProxyRepo.findAll());
        hechosTotales.addAll(hechosEstaticaRepo.findAll());
        return hechosTotales;
    }




}
