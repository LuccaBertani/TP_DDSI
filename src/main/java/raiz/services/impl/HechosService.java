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
import raiz.models.repositories.IColeccionRepository;
import raiz.models.repositories.IHechosRepository;
import raiz.models.repositories.IPersonaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import raiz.services.IHechosService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HechosService implements IHechosService {


    private final IHechosRepository hechosRepo;
    private final IPersonaRepository usuariosRepo;
    private final IColeccionRepository coleccionRepo;

    public HechosService(IHechosRepository repo, IPersonaRepository usuariosRepo, IColeccionRepository coleccionRepo) {
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
                Pais pais = BuscadorPais.buscar(hechos,dtoInput.getPais());
                hecho.setPais(pais);
            }else{
                Pais pais = BuscadorPais.buscar(hechos,"N/A");
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
                Categoria categoria = BuscadorCategoria.buscar(hechos,dtoInput.getCategoria());
                hecho.setCategoria(categoria);
            }
            else{
                Categoria categoria = BuscadorCategoria.buscar(hechos,"N/A");
                hecho.setCategoria(categoria);
            }
            hecho.setOrigen(Origen.CARGA_MANUAL);
            hecho.setId(hechosRepo.getProxId());
            hecho.setActivo(true);
            hecho.setFechaDeCarga(ZonedDateTime.now());
            hechosRepo.getSnapshotHechos().add(hecho);
            hechosRepo.save(hecho);
            this.mapearHechoAColecciones(hecho);
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

            List<Hecho> hechosActuales = hechosRepo.findAll();

            FuenteEstatica fuente = new FuenteEstatica();
            fuente.setDataSet(dtoInput.getFuenteString());
            ModificadorHechos modificadorHechos = fuente.leerFuente(hechosActuales);

            List<Hecho> hechosASubir = modificadorHechos.getHechosASubir();
            List<Hecho> hechosAModificar = modificadorHechos.getHechosAModificar();

            for (Hecho hecho : hechosASubir){
                hecho.setId(hechosRepo.getProxId());
                hechosRepo.getSnapshotHechos().add(hecho);
                hechosRepo.save(hecho);
                this.mapearHechoAColecciones(hecho);
            }
            for (Hecho hecho : hechosAModificar){
                hechosRepo.getSnapshotHechos().add(hecho);
                hechosRepo.update(hecho);
                this.mapearHechoAColecciones(hecho);
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

        List<Hecho> hechosFiltrados = Filtrador
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
