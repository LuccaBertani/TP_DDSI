package modulos.agregacion.services;


import modulos.agregacion.entities.*;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaAbsoluta;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaSimple;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMultiplesMenciones;
import modulos.agregacion.repositories.*;
import modulos.agregacion.entities.fuentes.Dataset;
import modulos.agregacion.entities.fuentes.FuenteEstatica;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.RespuestaHttp;
import modulos.agregacion.entities.AtributosHecho;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorHecho;
import modulos.buscadores.BuscadorPais;
import modulos.buscadores.BuscadorProvincia;
import modulos.shared.dtos.input.*;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.agregacion.entities.usuario.Rol;
import modulos.agregacion.entities.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import modulos.shared.dtos.output.ColeccionOutputDTO;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ColeccionService  {

    private final IHechosProxyRepository hechosProxyRepo;
    private final IHechosEstaticaRepository hechosEstaticaRepo;
    private final IHechosDinamicaRepository hechosDinamicaRepo;
    private final IHechoRepository hechoRepository;
    private final IColeccionRepository coleccionesRepo;
    private final IUsuarioRepository usuariosRepo;
    private final IDatasetsRepository datasetsRepo;
    private final ICategoriaRepository categoriaRepo;
    private final BuscadorProvincia buscadorProvincia;
    private final BuscadorPais buscadorPais;
    private final BuscadorCategoria buscadorCategoria;
    private final BuscadorHecho buscadorHecho;

    public ColeccionService(IHechosProxyRepository hechosProxyRepo,
                            IHechosEstaticaRepository hechosEstaticaRepo,
                            IHechosDinamicaRepository hechosDinamicaRepo,
                            IColeccionRepository coleccionesRepo,
                            IUsuarioRepository usuariosRepo,
                            IDatasetsRepository datasetsRepo,
                            IHechoRepository hechoRepository,
                            ICategoriaRepository categoriaRepo,
                            BuscadorProvincia buscadorProvincia,
                            BuscadorPais buscadorPais,
                            BuscadorCategoria buscadorCategoria,
                            BuscadorHecho buscadorHecho) {
        this.hechosProxyRepo = hechosProxyRepo;
        this.hechosEstaticaRepo = hechosEstaticaRepo;
        this.hechosDinamicaRepo = hechosDinamicaRepo;
        this.coleccionesRepo = coleccionesRepo;
        this.usuariosRepo = usuariosRepo;
        this.datasetsRepo = datasetsRepo;
        this.hechoRepository = hechoRepository;
        this.categoriaRepo = categoriaRepo;
        this.buscadorProvincia = buscadorProvincia;
        this.buscadorPais = buscadorPais;
        this.buscadorCategoria = buscadorCategoria;
        this.buscadorHecho = buscadorHecho;
    }


    /*
    Colecciones
Las colecciones representan conjuntos de hechos. Las mismas pueden ser consultadas por cualquier persona, de forma
pública, y no pueden ser editadas ni eliminadas manualmente (esto último, con una sola excepción, ver más adelante).

Las colecciones tienen un título, como por ejemplo “Desapariciones vinculadas a crímenes de odio”, o “Incendios
forestales en Argentina 2025” y una descripción. Las personas administradoras pueden crear tantas colecciones como deseen.

Las colecciones están asociadas a una fuente y tomarán los hechos de las mismas: para esto las colecciones también contarán con un criterio de
pertenencia configurable, que dictará si un hecho pertenece o no a las mismas. Por ejemplo, la colección de “Incendios forestales…” deberá
incluir automáticamente todos los hechos de categoría “Incendio forestal” ocurrido en Argentina, acontecido entre el 1 de enero de 2025 a las
0:00 y el 31 de diciembre de 20205 a las 23:59.

//TODO...  por ahora se desea que nuestras fuentes proxy también sean capaces de consumir este tipo de API.......

    */

    public RespuestaHttp<Void> crearColeccion(ColeccionInputDTO dtoInput) {

        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario()).orElse(null);


        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        DatosColeccion datosColeccion = new DatosColeccion(dtoInput.getTitulo(), dtoInput.getDescripcion());

        Coleccion coleccion = new Coleccion(datosColeccion);


        FormateadorHecho formateador = new FormateadorHecho();

        FiltrosColeccion filtros = formateador.formatearFiltrosColeccion(buscadorCategoria, buscadorPais, buscadorProvincia, dtoInput.getCriterios());


        if (dtoInput.getAlgoritmoConsenso() != null){
            switch (dtoInput.getAlgoritmoConsenso()) {
                case "mayoria-absoluta" ->
                        coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaAbsoluta());
                case "mayoria-simple" -> coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaSimple());
                case "multiples-menciones" ->
                        coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMultiplesMenciones());
            }
        }

        coleccion.setCriterios(formateador.obtenerListaDeFiltros(filtros));

        List<Hecho> hechos = hechoRepository.findAll();

        coleccion.addHechos(Filtrador.aplicarFiltros(coleccion.getCriterios(), hechos));
        coleccion.setModificado(true);
        coleccionesRepo.save(coleccion);

        return new RespuestaHttp<>(null, HttpStatus.CREATED.value());

    }

    public RespuestaHttp<List<ColeccionOutputDTO>> obtenerTodasLasColecciones(){

        List<ColeccionOutputDTO> listaDTO = new ArrayList<>();

        List<Coleccion> colecciones = coleccionesRepo.findAll();

        for (Coleccion coleccion : colecciones){
            ColeccionOutputDTO dto = new ColeccionOutputDTO();
            dto.setId(coleccion.getId());
            dto.setTitulo(coleccion.getTitulo());
            dto.setDescripcion(coleccion.getDescripcion());

            FormateadorHecho formateadorHecho = new FormateadorHecho();

            CriteriosColeccionDTO criterios = formateadorHecho.filtrosColeccionToString(coleccion.getCriterios());

            dto.setCriterios(criterios);

            listaDTO.add(dto);
        }
        return new RespuestaHttp<>(listaDTO, HttpStatus.OK.value());
    }

    public RespuestaHttp<ColeccionOutputDTO> getColeccion(Long id_coleccion) {

        Coleccion coleccion = coleccionesRepo.findById(id_coleccion).orElse(null);

        if(coleccion == null){
            return new RespuestaHttp<>(null,HttpStatus.NO_CONTENT.value());
        }

        ColeccionOutputDTO dto = new ColeccionOutputDTO();

        dto.setId(coleccion.getId());
        dto.setTitulo(coleccion.getTitulo());
        dto.setDescripcion(coleccion.getDescripcion());

        FormateadorHecho formateadorHecho = new FormateadorHecho();

        CriteriosColeccionDTO criterios = formateadorHecho.filtrosColeccionToString(coleccion.getCriterios());

        dto.setCriterios(criterios);

        return new RespuestaHttp<>(dto, HttpStatus.OK.value());
    }

    public RespuestaHttp<ColeccionOutputDTO> deleteColeccion(Long id_coleccion) {
        Coleccion coleccion = coleccionesRepo.findById(id_coleccion).orElse(null);
        if(coleccion == null){
            return new RespuestaHttp<>(null,HttpStatus.NO_CONTENT.value());
        }
        coleccion.setActivo(false);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }


    public RespuestaHttp<Void> agregarFuente(Long idColeccion, String dataSet) {
        Coleccion coleccion = coleccionesRepo.findById(idColeccion).orElse(null);
        FuenteEstatica fuente = new FuenteEstatica();
        Dataset dataset = new Dataset(dataSet);
        fuente.setDataSet(dataset);

        List<HechoEstatica> hechosFuente = fuente.leerFuente(buscadorCategoria, buscadorPais, buscadorProvincia, buscadorHecho);
        List<Hecho> hechos = new ArrayList<>(hechosFuente);
        coleccion.addHechos(hechos);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    public RespuestaHttp<Void> eliminarFuente(Long idColeccion, String datasetString) {
        Coleccion coleccion = coleccionesRepo.findById(idColeccion).orElse(null);
        Dataset dataset = new Dataset(datasetString);
        coleccion.getHechos().forEach(
                hecho -> {
                    if(hecho.getDatasets().contains(dataset)){
                        coleccion.getHechos().remove(hecho);
                    }
                }
        );
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    public RespuestaHttp<Void> updateColeccion(ColeccionUpdateInputDTO dto) {
        Coleccion coleccion = coleccionesRepo.findById(dto.getId_coleccion()).orElse(null);
        Usuario usuario = usuariosRepo.findById(dto.getId_usuario()).orElse(null);
        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        if(coleccion == null){
            return new RespuestaHttp<>(null, HttpStatus.NO_CONTENT.value());
        }
        CriteriosColeccionDTO criterios = new CriteriosColeccionDTO(dto.getCategoria(),dto.getContenidoMultimedia().toString(),dto.getDescripcion(),dto.getFechaAcontecimientoInicial(),dto.getFechaAcontecimientoFinal(),dto.getFechaCargaInicial(),dto.getFechaCargaFinal(),dto.getOrigen().toString(),dto.getPais(),dto.getTitulo());
        FormateadorHecho formateador = new FormateadorHecho();
        FiltrosColeccion filtrosColeccion = formateador.formatearFiltrosColeccion(buscadorCategoria, buscadorPais, buscadorProvincia, criterios);
        List<Hecho> hechosColeccion = new ArrayList<>();
        for(VisualizarHechosOutputDTO hecho : dto.getHechos()){
            SolicitudHechoInputDTO dto1 = new SolicitudHechoInputDTO();
            dto1.setPais(hecho.getPais());
            dto1.setTipoContenido(hecho.getContenidoMultimedia());
            dto1.setTitulo(hecho.getTitulo());
            dto1.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
            dto1.setDescripcion(hecho.getDescripcion());
            dto1.setCategoria(hecho.getCategoria());

            Optional<Categoria> categoria = categoriaRepo.findByNombreNormalizado(dto.getCategoria());



            AtributosHecho atributos = formateador.formatearAtributosHecho(buscadorCategoria, buscadorPais, buscadorProvincia, dto1);
            HechoDinamica hecho1 = new HechoDinamica();
            hecho1.setAtributosHecho(atributos);
            hechosColeccion.add((Hecho)hecho1);
        }
        coleccion.setModificado(true);
        coleccion.actualizar(dto,formateador.obtenerListaDeFiltros(filtrosColeccion),hechosColeccion);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void setearHechosConsensuados(){
        List<Coleccion> colecciones = coleccionesRepo.findAll();
        List<Dataset> datasets = datasetsRepo.findAll();
        this.ejecutarAlgoritmoConsenso(colecciones, datasets);
    }

    private void ejecutarAlgoritmoConsenso(List<Coleccion> colecciones, List<Dataset> datasets){
        colecciones.forEach(coleccion->coleccion.getAlgoritmoConsenso().ejecutarAlgoritmoConsenso(datasets, coleccion));
    }

    public RespuestaHttp<Void> modificarAlgoritmoConsenso(ModificarConsensoInputDTO input) {
        Coleccion coleccion = coleccionesRepo.findById(input.getIdColeccion()).orElse(null);
        Usuario usuario = usuariosRepo.findById(input.getIdUsuario()).orElse(null);

        if (coleccion == null || usuario == null) {
            return new RespuestaHttp<>(null, HttpStatus.BAD_REQUEST.value()); // Datos inválidos en la solicitud
        }
        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)) {
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        switch (input.getTipoConsenso()) {
            case "mayoria-absoluta":
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaAbsoluta());
                break;
            case "mayoria-simple":
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaSimple());
                break;
            case "multiples-menciones":
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMultiplesMenciones());
                break;
            default:
                return new RespuestaHttp<>(null, HttpStatus.BAD_REQUEST.value());
        }
        coleccionesRepo.save(coleccion);
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }
}
