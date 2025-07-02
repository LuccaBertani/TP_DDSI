package modulos.agregacion.services;


import modulos.agregacion.entities.*;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaAbsoluta;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaSimple;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMultiplesMenciones;
import modulos.agregacion.repositories.*;
import modulos.fuentes.Dataset;
import modulos.fuentes.FuenteEstatica;
import modulos.shared.Hecho;
import modulos.shared.RespuestaHttp;
import modulos.shared.dtos.AtributosHecho;
import modulos.shared.dtos.input.*;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.usuario.Rol;
import modulos.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import modulos.shared.dtos.output.ColeccionOutputDTO;
import java.util.ArrayList;
import java.util.List;

@Service
public class ColeccionService  {

    private final IHechosProxyRepository hechosProxyRepo;
    private final IHechosEstaticaRepository hechosEstaticaRepo;
    private final IHechosDinamicaRepository hechosDinamicaRepo;
    private final IColeccionRepository coleccionesRepo;
    private final IUsuarioRepository usuariosRepo;
    private final IDatasetsRepository datasetsRepo;

    public ColeccionService(IHechosProxyRepository hechosProxyRepo, IHechosEstaticaRepository hechosEstaticaRepo, IHechosDinamicaRepository hechosDinamicaRepo, IColeccionRepository coleccionesRepo, IUsuarioRepository usuariosRepo, IDatasetsRepository datasetsRepo) {
        this.hechosProxyRepo = hechosProxyRepo;
        this.hechosEstaticaRepo = hechosEstaticaRepo;
        this.hechosDinamicaRepo = hechosDinamicaRepo;
        this.coleccionesRepo = coleccionesRepo;
        this.usuariosRepo = usuariosRepo;
        this.datasetsRepo = datasetsRepo;
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

        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        DatosColeccion datosColeccion = new DatosColeccion(dtoInput.getTitulo(), dtoInput.getDescripcion());

        Coleccion coleccion = new Coleccion(datosColeccion,coleccionesRepo.getProxId());


        FormateadorHecho formateador = new FormateadorHecho();

        FiltrosColeccion filtros = formateador.formatearFiltrosColeccion(hechosDinamicaRepo.findAll(),hechosEstaticaRepo.findAll(),hechosProxyRepo.findAll(),dtoInput.getCriterios());


        if (dtoInput.getAlgoritmoConsenso() != null){
            switch (dtoInput.getAlgoritmoConsenso()) {
                case "mayoria-absoluta" ->
                        coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaAbsoluta(coleccion));
                case "mayoria-simple" -> coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaSimple(coleccion));
                case "multiples-menciones" ->
                        coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMultiplesMenciones(coleccion));
            }
        }

        coleccion.setCriterios(formateador.obtenerMapaDeFiltros(filtros));

        List<Hecho> hechosDinamica = hechosDinamicaRepo.findAll();
        List<Hecho> hechosEstatica = hechosEstaticaRepo.findAll();
        List<Hecho> hechosProxy = hechosProxyRepo.findAll();

        coleccion.addHechos(Filtrador.aplicarFiltros(formateador.obtenerMapaDeFiltros(filtros), hechosDinamica));
        coleccion.addHechos(Filtrador.aplicarFiltros(formateador.obtenerMapaDeFiltros(filtros), hechosEstatica));
        coleccion.addHechos(Filtrador.aplicarFiltros(formateador.obtenerMapaDeFiltros(filtros), hechosProxy));

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

        Coleccion coleccion = coleccionesRepo.findById(id_coleccion);

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
        Coleccion coleccion = coleccionesRepo.findById(id_coleccion);
        if(coleccion == null){
            return new RespuestaHttp<>(null,HttpStatus.NO_CONTENT.value());
        }
        coleccion.setActivo(false);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }


    public RespuestaHttp<Void> agregarFuente(Long idColeccion, String dataSet) {
        Coleccion coleccion = coleccionesRepo.findById(idColeccion);
        FuenteEstatica fuente = new FuenteEstatica();
        Dataset dataset = new Dataset(dataSet, datasetsRepo.getProxId());
        fuente.setDataSet(dataset);

        List<Hecho> hechosFuente = fuente.leerFuente(hechosProxyRepo.findAll(),hechosDinamicaRepo.findAll(), hechosEstaticaRepo.findAll());
        coleccion.addHechos(hechosFuente);

        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    public RespuestaHttp<Void> eliminarFuente(Long idColeccion, String datasetString) {
        Coleccion coleccion = coleccionesRepo.findById(idColeccion);
        Dataset dataset = new Dataset(datasetString, datasetsRepo.getProxId());
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
        Coleccion coleccion = coleccionesRepo.findById(dto.getId_coleccion());
        Usuario usuario = usuariosRepo.findById(dto.getId_usuario());
        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }
        if(coleccion == null){
            return new RespuestaHttp<>(null, HttpStatus.NO_CONTENT.value());
        }
        CriteriosColeccionDTO criterios = new CriteriosColeccionDTO(dto.getCategoria(),dto.getContenidoMultimedia().toString(),dto.getDescripcion(),dto.getFechaAcontecimientoInicial(),dto.getFechaAcontecimientoFinal(),dto.getFechaCargaInicial(),dto.getFechaCargaFinal(),dto.getOrigen().toString(),dto.getPais(),dto.getTitulo());
        FormateadorHecho formateador = new FormateadorHecho();
        FiltrosColeccion filtrosColeccion = formateador.formatearFiltrosColeccion(hechosDinamicaRepo.findAll(),hechosEstaticaRepo.findAll(),hechosProxyRepo.findAll(),criterios);
        List<Hecho> hechosColeccion = new ArrayList<>();
        for(VisualizarHechosOutputDTO hecho : dto.getHechos()){
            SolicitudHechoInputDTO dto1 = new SolicitudHechoInputDTO();
            dto1.setPais(hecho.getPais());
            dto1.setTipoContenido(hecho.getContenidoMultimedia());
            dto1.setTitulo(hecho.getTitulo());
            dto1.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
            dto1.setDescripcion(hecho.getDescripcion());
            dto1.setCategoria(hecho.getCategoria());

            AtributosHecho atributos = formateador.formatearAtributosHecho(hechosDinamicaRepo.findAll(),hechosEstaticaRepo.findAll(),hechosProxyRepo.findAll(),dto1);
            Hecho hecho1 = new Hecho();
            hecho1.setAtributosHecho(atributos);
            hechosColeccion.add(hecho1);
        }

        coleccion.actualizar(dto,formateador.obtenerMapaDeFiltros(filtrosColeccion),hechosColeccion);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void setearHechosConsensuados(){
        List<Coleccion> colecciones = coleccionesRepo.findAll();
        List<Dataset> datasets = datasetsRepo.findAll();
        this.ejecutarAlgoritmoConsenso(colecciones, datasets);
    }

    private void ejecutarAlgoritmoConsenso(List<Coleccion> colecciones, List<Dataset> datasets){
        colecciones.forEach(coleccion->coleccion.getAlgoritmoConsenso().ejecutarAlgoritmoConsenso(datasets));
    }

    public RespuestaHttp<Void> modificarAlgoritmoConsenso(ModificarConsensoInputDTO input) {
        Coleccion coleccion = coleccionesRepo.findById(input.getIdColeccion());
        Usuario usuario = usuariosRepo.findById(input.getIdUsuario());

        if (coleccion == null || usuario == null) {
            return new RespuestaHttp<>(null, HttpStatus.BAD_REQUEST.value()); // Datos inválidos en la solicitud
        }
        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)) {
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        switch (input.getTipoConsenso()) {
            case "mayoria-absoluta":
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaAbsoluta(coleccion));
                break;
            case "mayoria-simple":
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaSimple(coleccion));
                break;
            case "multiples-menciones":
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMultiplesMenciones(coleccion));
                break;
            default:
                return new RespuestaHttp<>(null, HttpStatus.BAD_REQUEST.value());
        }
        coleccionesRepo.update(coleccion);
        return new RespuestaHttp<>(null, HttpStatus.OK.value());
    }
}
