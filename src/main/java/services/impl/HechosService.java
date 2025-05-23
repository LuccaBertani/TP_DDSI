package services.impl;

import models.dtos.input.ImportacionHechosInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.dtos.input.VisualizarHechosInputDTO;
import models.dtos.output.VisualizarHechosOutputDTO;
import models.entities.*;
import models.entities.buscadores.BuscadorCategoria;
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
import org.springframework.stereotype.Service;
import services.IColeccionService;
import services.IHechosService;

import java.sql.Array;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public RespuestaHttp<Integer> subirHecho(SolicitudHechoInputDTO dtoInput) {
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if(usuario.getRol().equals(Rol.ADMINISTRADOR)){

            // TODO: METER ESTE ALGORITMO QUE CHEQUEA PAIS EXISTENTE Y CREA UN HECHO DESDE 0 EN UN METODO APARTE
            Hecho hecho = new Hecho();

            List<Hecho> hechos = hechosRepo.findAll(); // TODO cambiar x la temporal

            Optional<Hecho> hecho2 = hechos.stream().filter(h->Normalizador.normalizarYComparar(h.getPais().getPais(), dtoInput.getPais())).findFirst();
            Pais pais;

            if (hecho2.isPresent()){
                pais = hecho2.get().getPais();
            } else {
                pais = new Pais();
                pais.setPais(dtoInput.getPais());
            }

            hecho.setTitulo(dtoInput.getTitulo());
            hecho.setDescripcion(dtoInput.getDescripcion());
            hecho.setPais(pais);
            hecho.setFechaAcontecimiento(FechaParser.parsearFecha(dtoInput.getFechaAcontecimiento()));
            hecho.setOrigen(Origen.CARGA_MANUAL);
            hecho.setId(hechosRepo.getProxId());
            hecho.setActivo(true);
            hecho.setFechaDeCarga(ZonedDateTime.now());
            hechosRepo.save(hecho);
            return new RespuestaHttp<>(-1, HttpStatus.OK.value());
        }
        else{
            return new RespuestaHttp<>(-1, HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    public RespuestaHttp<Integer> importarHechos(ImportacionHechosInputDTO dtoInput){
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){
            // Se borran y suben hechos constantemente => Guardamos los que se tienen hasta el momento en una lista
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
            return new RespuestaHttp<>(-1, HttpStatus.OK.value());
        }

        return new RespuestaHttp<>(-1, HttpStatus.UNAUTHORIZED.value());

    }

    @Override
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(List<String> filtros, Long id_coleccion){

        List<Filtro> filter = new ArrayList<>();

        if(!filtros.get(0).equals("N/A")){


            Filtro filtroCategoria = new FiltroCategoria(BuscadorCategoria.buscar(hechosRepo.findAll(),filtros.get(0)));
            filter.add(filtroCategoria);
        }
        if(!filtros.get(1).equals("N/A")){
            TipoContenido contenido = TipoContenido.fromCodigo(Integer.parseInt(filtros.get(1)));
            Filtro filtroContenidoMultimedia = new FiltroContenidoMultimedia(contenido);
            filter.add(filtroContenidoMultimedia);
        }
        if(!filtros.get(2).equals("N/A")){
            Filtro filtroDescripcion = new FiltroDescripcion(filtros.get(2));
            filter.add(filtroDescripcion);
        }
        if(!filtros.get(3).equals("N/A") && !filtros.get(4).equals("N/A")){
            ZonedDateTime fechaInicial = FechaParser.parsearFecha(filtros.get(3));
            ZonedDateTime fechaFinal = FechaParser.parsearFecha(filtros.get(4));
            Filtro filtroFechaAcontecimiento = new FiltroFechaAcontecimiento(fechaInicial,fechaFinal);
            filter.add(filtroFechaAcontecimiento);
        }
        if(!filtros.get(5).equals("N/A") && !filtros.get(6).equals("N/A")){
            ZonedDateTime fechaInicial = FechaParser.parsearFecha(filtros.get(5));
            ZonedDateTime fechaFinal = FechaParser.parsearFecha(filtros.get(6));
            Filtro filtroFechaCarga = new FiltroFechaCarga(fechaInicial,fechaFinal);
            filter.add(filtroFechaCarga);
        }
        if(!filtros.get(7).equals("N/A")){
            Origen origen = Origen.fromCodigo(Integer.parseInt(filtros.get(7)));
            Filtro filtroOrigen = new FiltroOrigen(origen);
            filter.add(filtroOrigen);
        }
        if(!filtros.get(8).equals("N/A")){

            Filtro filtroPais = new FiltroPais(BuscadorPais.buscar(hechosRepo.findAll(),filtros.get(8)));
            filter.add(filtroPais);

        }
        if(!filtros.get(9).equals("N/A")){
            Filtro filtroTitulo = new FiltroTitulo(filtros.get(9));
            filter.add(filtroTitulo);
        }

        Filtrador filtrador = new Filtrador();

        List<Hecho> lista = filtrador.aplicarFiltros(filter, coleccionRepo.findById(id_coleccion).getHechos());

        List<VisualizarHechosOutputDTO> outputDTO = new ArrayList<>();

        for(Hecho hecho : lista){

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
