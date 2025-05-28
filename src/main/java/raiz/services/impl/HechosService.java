package raiz.services.impl;

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
import raiz.models.repositories.IColeccionRepository;
import raiz.models.repositories.IHechosRepository;
import raiz.models.repositories.IUsuarioRepository;
import raiz.services.IHechosService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HechosService implements IHechosService {


    private final IHechosRepository hechosRepo;
    private final IUsuarioRepository usuariosRepo;
    private final IColeccionRepository coleccionRepo;

    public HechosService(IHechosRepository repo, IUsuarioRepository usuariosRepo, IColeccionRepository coleccionRepo) {
        this.hechosRepo = repo;
        this.usuariosRepo = usuariosRepo;
        this.coleccionRepo = coleccionRepo;
    }

    /* Se pide que, una vez por hora, el servicio de agregación actualice los hechos pertenecientes a las distintas colecciones,
     en caso de que las fuentes hayan incorporado nuevos hechos.*/
    @Override
    @Scheduled(cron = "0 0 * * * *") // cada hora
    public void refrescarColecciones() {
        List<Hecho> snapshotHechos = hechosRepo.getSnapshotHechos();
        if (!snapshotHechos.isEmpty()){
            this.mapearHechosAColecciones(snapshotHechos);
            hechosRepo.clearSnapshotHechos();
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

    @Override
    public RespuestaHttp<Void> subirHecho(SolicitudHechoInputDTO dtoInput) {
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if(usuario.getRol().equals(Rol.ADMINISTRADOR)){

            Hecho hecho = new Hecho();

            List<Hecho> hechos = hechosRepo.findAll();

            if(dtoInput.getPais() != null) {
                Pais pais = BuscadorPais.buscarOCrear(hechos,dtoInput.getPais());
                hecho.setPais(pais);
            }else{
                Pais pais = BuscadorPais.buscarOCrear(hechos,"N/A");
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
                Categoria categoria = BuscadorCategoria.buscarOCrear(hechos,dtoInput.getCategoria());
                hecho.setCategoria(categoria);
            }
            else{
                Categoria categoria = BuscadorCategoria.buscarOCrear(hechos,"N/A");
                hecho.setCategoria(categoria);
            }
            hecho.setOrigen(Origen.CARGA_MANUAL);
            hecho.setId(hechosRepo.getProxId());
            hecho.setActivo(true);
            hecho.setFechaDeCarga(ZonedDateTime.now());
            hecho.setFechaUltimaActualizacion(hecho.getFechaDeCarga());
            this.mapearHechoAColecciones(hecho);
            hechosRepo.save(hecho);
            this.mapearHechoAColecciones(hecho);
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

            List<Hecho> hechosActuales = hechosRepo.findAll();

            FuenteEstatica fuente = new FuenteEstatica();
            fuente.setDataSet(dtoInput.getFuenteString());
            ModificadorHechos modificadorHechos = fuente.leerFuente(hechosActuales);

            List<Hecho> hechosASubir = modificadorHechos.getHechosASubir();
            List<Hecho> hechosAModificar = modificadorHechos.getHechosAModificar();

            for (Hecho hecho : hechosASubir){
                hecho.setId(hechosRepo.getProxId());
                this.mapearHechoAColecciones(hecho);
                hechosRepo.save(hecho);
            }
            for (Hecho hecho : hechosAModificar){
                this.mapearHechoAColecciones(hecho);
                hechosRepo.update(hecho);
            }
            return new RespuestaHttp<>(null, HttpStatus.CREATED.value());
        }

        return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());

    }

    @Override
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(FiltroHechosDTO inputDTO) {
        List<Hecho> hechosTotales = hechosRepo.findAll();
        List<Filtro> filter = new ArrayList<>();
        String categoriaString = inputDTO.getCategoria();
        if (categoriaString != null) {
            Categoria categoria = BuscadorCategoria.buscar(hechosTotales, categoriaString);
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
            Pais pais = BuscadorPais.buscar(hechosTotales, paisString);
            if (pais!=null)
                filter.add(new FiltroPais(pais));
        }

        if (inputDTO.getTitulo() != null) {
            filter.add(new FiltroTitulo(inputDTO.getTitulo()));
        }

        List<Hecho> hechosFiltrados = Filtrador
                .aplicarFiltros(filter, hechosRepo.findAll());

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
        return hechosRepo.findAll();
    }


}
