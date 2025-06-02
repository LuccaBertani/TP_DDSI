package raiz.services.impl;

import org.springframework.scheduling.annotation.Async;
import raiz.models.dtos.input.FiltroHechosDTO;
import raiz.models.dtos.input.ImportacionHechosInputDTO;
import raiz.models.dtos.input.SolicitudHechoInputDTO;
import raiz.models.dtos.output.VisualizarHechosOutputDTO;
import raiz.models.entities.*;
import raiz.models.entities.buscadores.BuscadorCategoria;
import raiz.models.entities.buscadores.BuscadorPais;
import raiz.models.entities.filtros.*;
import raiz.models.entities.fuentes.FuenteEstatica;
import raiz.models.entities.personas.Rol;
import raiz.models.entities.personas.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import raiz.models.repositories.*;
import raiz.services.IHechosService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class HechosService implements IHechosService {


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
    @Override
    @Async
    @Scheduled(cron = "0 0 * * * *") // cada hora
    public void refrescarColeccionesCronjob() {
        mutexRefrescarColecciones.lock();
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
        mutexRefrescarColecciones.unlock();
    }

    public RespuestaHttp<Void> refrescarColecciones(Long idUsuario){
        Usuario usuario = usuariosRepo.findById(idUsuario);
        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        this.refrescarColeccionesCronjob();
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }

    private void eliminarHechosModificadosDeColecciones(List<Coleccion> colecciones, List<Hecho> hechos){

        for (Coleccion coleccion: colecciones){
            List<Hecho> hechosColeccion = coleccion.getHechos();
            List<Filtro> criterio = coleccion.getCriterio();
            for (Hecho hecho: hechos){

                Long hechoId = hecho.getId();
                Origen hechoOrigen = hecho.getOrigen();

                Hecho hechoEncontrado = hechosColeccion.stream()
                                        .filter(h -> h.getId().equals(hechoId) && h.getOrigen().equals(hechoOrigen))
                                        .findFirst().orElse(null);
                if (hechoEncontrado != null && !Filtrador.hechoPasaFiltros(criterio, hecho)){
                    hechosColeccion.remove(hecho);
                }
            }
        }

    }


    @Override
    public void mapearHechosAColecciones(List<Hecho> hechos){

        List<Coleccion> colecciones = coleccionRepo.findAll();

        for (Coleccion coleccion : colecciones){
            List<Filtro> filtros = coleccion.getCriterio();
            List<Hecho> hechosFiltrados = Filtrador.aplicarFiltros(filtros, hechos);
            coleccion.addHechos(hechosFiltrados);
        }
    }

    @Override
    public void mapearHechoAColecciones(Hecho hecho){
        List<Coleccion> colecciones = coleccionRepo.findAll();

        for (Coleccion coleccion : colecciones){
            List<Filtro> filtros = coleccion.getCriterio();

            boolean cumpleCriterio = Filtrador.hechoPasaFiltros(filtros, hecho);

            if (cumpleCriterio){
                coleccion.addHechos(hecho);
            }

        }
    }
    //lo sube un administrador (lo considero carga dinamica)
    @Override
    public RespuestaHttp<Void> subirHecho(SolicitudHechoInputDTO dtoInput) {
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if(usuario.getRol().equals(Rol.ADMINISTRADOR)){

            Hecho hecho = new Hecho();

            if(dtoInput.getPais() != null) {
                Pais pais = BuscadorPais.buscarOCrear(hechosDinamicaRepo.findAll(),dtoInput.getPais(),hechosProxyRepo.findAll(),hechosEstaticaRepo.findAll());
                hecho.setPais(pais);
            }else{
                Pais pais = BuscadorPais.buscarOCrear(hechosDinamicaRepo.findAll(),"N/A",hechosProxyRepo.findAll(),hechosEstaticaRepo.findAll());
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
                Categoria categoria = BuscadorCategoria.buscarOCrear(hechosDinamicaRepo.findAll(),dtoInput.getPais(),hechosProxyRepo.findAll(),hechosEstaticaRepo.findAll());
                hecho.setCategoria(categoria);
            }
            else{
                Categoria categoria = BuscadorCategoria.buscarOCrear(hechosDinamicaRepo.findAll(),"N/A",hechosProxyRepo.findAll(),hechosEstaticaRepo.findAll());
                hecho.setCategoria(categoria);
            }
            hecho.setOrigen(Origen.CARGA_MANUAL);
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

    @Override
    public RespuestaHttp<Void> importarHechos(ImportacionHechosInputDTO dtoInput){
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){

            FuenteEstatica fuente = new FuenteEstatica();
            fuente.setDataSet(dtoInput.getFuenteString());

            ModificadorHechos modificadorHechos = fuente.leerFuente(hechosDinamicaRepo.findAll(),hechosProxyRepo.findAll(),hechosEstaticaRepo.findAll());

            List<Hecho> hechosASubir = modificadorHechos.getHechosASubir();
            List<Hecho> hechosAModificar = modificadorHechos.getHechosAModificar();

            for (Hecho hecho : hechosASubir){
                hecho.setId(hechosEstaticaRepo.getProxId());
                hechosEstaticaRepo.getSnapshotHechos().add(hecho);
                hechosEstaticaRepo.save(hecho);
            }
            //TODO agregar a snapshot y que verifique si es repetido y en ese caso actualizar en la base de datos (aparte de la logica de agregarlo a las colecciones)
            for (Hecho hecho : hechosAModificar){
                this.mapearHechoAColecciones(hecho);
                hechosEstaticaRepo.update(hecho);
            }
            return new RespuestaHttp<>(null, HttpStatus.CREATED.value());
        }

        return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());

    }

    @Override
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(FiltroHechosDTO inputDTO) {

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





        List<Hecho> hechosFiltrados = Filtrador
                .aplicarFiltros(filter, this.getAllHechos());

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

    @Override
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
