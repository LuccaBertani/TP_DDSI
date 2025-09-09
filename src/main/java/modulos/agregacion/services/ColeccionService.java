package modulos.agregacion.services;


import modulos.agregacion.entities.*;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaAbsoluta;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaSimple;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMultiplesMenciones;
import modulos.agregacion.entities.filtros.Filtro;
import modulos.agregacion.repositories.*;
import modulos.agregacion.entities.fuentes.Dataset;
import modulos.agregacion.entities.fuentes.FuenteEstatica;
import modulos.agregacion.entities.Hecho;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorHecho;
import modulos.buscadores.BuscadorPais;
import modulos.buscadores.BuscadorProvincia;
import modulos.shared.dtos.input.*;
import modulos.agregacion.entities.usuario.Rol;
import modulos.agregacion.entities.usuario.Usuario;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import modulos.shared.dtos.output.ColeccionOutputDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ColeccionService  {

    private final IHechoRepository hechoRepository;
    private final IColeccionRepository coleccionesRepo;
    private final IUsuarioRepository usuariosRepo;
    private final IDatasetsRepository datasetsRepo;
    private final BuscadorProvincia buscadorProvincia;
    private final BuscadorPais buscadorPais;
    private final BuscadorCategoria buscadorCategoria;
    private final BuscadorHecho buscadorHecho;

    public ColeccionService(IColeccionRepository coleccionesRepo,
                            IUsuarioRepository usuariosRepo,
                            IDatasetsRepository datasetsRepo,
                            IHechoRepository hechoRepository,
                            BuscadorProvincia buscadorProvincia,
                            BuscadorPais buscadorPais,
                            BuscadorCategoria buscadorCategoria,
                            BuscadorHecho buscadorHecho) {
        this.coleccionesRepo = coleccionesRepo;
        this.usuariosRepo = usuariosRepo;
        this.datasetsRepo = datasetsRepo;
        this.hechoRepository = hechoRepository;
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

    */

    public ResponseEntity<?> crearColeccion(ColeccionInputDTO dtoInput) {

        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario()).orElse(null);

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body("No tenés permisos para ejecutar esta acción");
        }

        DatosColeccion datosColeccion = new DatosColeccion(dtoInput.getTitulo(), dtoInput.getDescripcion());

        Coleccion coleccion = new Coleccion(datosColeccion);

        coleccion.setActivo(true);
        coleccion.setModificado(false);

        System.out.println("PORONGA 1");
        FiltrosColeccion filtros = FormateadorHecho.formatearFiltrosColeccionDinamica(buscadorCategoria, buscadorPais, buscadorProvincia, dtoInput.getCriterios());
        System.out.println("PORONGA 2");
//todo endpoint get all algoritmo consenso con (nombre)
        if (dtoInput.getAlgoritmoConsenso() != null){
            switch (dtoInput.getAlgoritmoConsenso()) {
                case "MAYORIA_ABSOLUTA":
                        coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaAbsoluta());
                        break;
                case "MAYORIA_SIMPLE":
                    coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaSimple());
                    break;
                case "MULTIPLES_MENCIONES":
                        coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMultiplesMenciones());
                        break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El algoritmo de consenso especificado no existe");
            }
        }

        coleccion.setCriterios(FormateadorHecho.obtenerListaDeFiltros(filtros));

        this.mapearHechosAColeccion(coleccion);

        return ResponseEntity.status(HttpStatus.CREATED).body("La colección se creó correctamente");

    }

    public ResponseEntity<?> obtenerTodasLasColecciones(){

        List<ColeccionOutputDTO> listaDTO = new ArrayList<>();

        List<Coleccion> colecciones = coleccionesRepo.findAll();

        for (Coleccion coleccion : colecciones){
            ColeccionOutputDTO dto = new ColeccionOutputDTO();
            dto.setId(coleccion.getId());
            dto.setTitulo(coleccion.getTitulo());
            dto.setDescripcion(coleccion.getDescripcion());

            ProxyDTO criterios = FormateadorHecho.filtrosColeccionToString(coleccion.getCriterios());
            dto.setCriterios(criterios);
            listaDTO.add(dto);
        }
        return ResponseEntity.status(HttpStatus.OK).body(listaDTO);
    }

    public ResponseEntity<?> getColeccion(Long id_coleccion) {
        System.out.println("PINGAAAAAAAAAAAAAAAAAA");
        Coleccion coleccion = coleccionesRepo.findById(id_coleccion).orElse(null);

        if(coleccion == null){
            System.out.println("soy null");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No se encontró la colección");

        }

        ColeccionOutputDTO dto = new ColeccionOutputDTO();

        dto.setId(coleccion.getId());
        dto.setTitulo(coleccion.getTitulo());
        dto.setDescripcion(coleccion.getDescripcion());

        ProxyDTO criterios = FormateadorHecho.filtrosColeccionToString(coleccion.getCriterios());

        dto.setCriterios(criterios);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    public ResponseEntity<?> deleteColeccion(Long id_coleccion, Long id_usuario) {

        Usuario usuario = usuariosRepo.findById(id_usuario).orElse(null);

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body("No tenés permisos para ejecutar esta acción");
        }

        Coleccion coleccion = coleccionesRepo.findById(id_coleccion).orElse(null);
        if(coleccion == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No se encontró la colección");
        }
        coleccion.setActivo(false);
        return ResponseEntity.status(HttpStatus.OK).body("Se ha borrado la colección");
    }


    public ResponseEntity<?> agregarFuente(Long id_usuario, Long idColeccion, String dataSet) {

        ResponseEntity<?> respuesta = checkeoAdmin(id_usuario);

        if(respuesta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return respuesta;
        }

        Coleccion coleccion = coleccionesRepo.findById(idColeccion).orElse(null);
        FuenteEstatica fuente = new FuenteEstatica();
        Dataset dataset = new Dataset(dataSet);
        fuente.setDataSet(dataset);

        List<HechoEstatica> hechosFuente = fuente.leerFuente(buscadorCategoria, buscadorPais, buscadorProvincia, buscadorHecho);
        List<Hecho> hechos = new ArrayList<>(hechosFuente);
        assert coleccion != null;
        coleccion.addHechos(hechos);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<?> eliminarFuente(Long id_usuario, Long idColeccion, String datasetString) {

        ResponseEntity<?> respuesta = checkeoAdmin(id_usuario);

        if(respuesta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return respuesta;
        }

        Coleccion coleccion = coleccionesRepo.findById(idColeccion).orElse(null);
        Dataset dataset = new Dataset(datasetString);
        assert coleccion != null;
        coleccion.getHechos().forEach(
                hecho -> {
                    if(hecho.getDatasets().contains(dataset)){
                        coleccion.getHechos().remove(hecho);
                    }
                }
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<?> updateColeccion(ColeccionUpdateInputDTO dto) {
        ResponseEntity<?> respuesta = checkeoAdmin(dto.getId_usuario());

        if(respuesta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return respuesta;
        }

        Coleccion coleccion = coleccionesRepo.findById(dto.getId_coleccion()).orElse(null);

        if (coleccion == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body("No se encontró la colección");
        }

        if (dto.getTitulo() != null){
            coleccion.setTitulo(dto.getTitulo());
        }

        if (dto.getDescripcion() != null){
            coleccion.setDescripcion(dto.getDescripcion());
        }

        FiltrosColeccion filtros = FormateadorHecho.formatearFiltrosColeccionDinamica(buscadorCategoria, buscadorPais, buscadorProvincia, dto.getCriterios());

        coleccion.setCriterios(FormateadorHecho.obtenerListaDeFiltros(filtros));

        this.mapearHechosAColeccion(coleccion);

        return ResponseEntity.status(HttpStatus.CREATED).build();
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

    public ResponseEntity<?> modificarAlgoritmoConsenso(ModificarConsensoInputDTO input) {

        Usuario usuario = usuariosRepo.findById(input.getIdUsuario()).orElse(null);

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body("No tenés permisos para ejecutar esta acción");
        }

        Coleccion coleccion = coleccionesRepo.findById(input.getIdColeccion()).orElse(null);
        if (coleccion == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la colección");
        }


        switch (input.getTipoConsenso()) {
            case "AlgoritmoConsensoMayoriaAbsoluta":
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaAbsoluta());
                break;
            case "AlgoritmoConsensoMayoriaSimple":
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaSimple());
                break;
            case "AlgoritmoConsensoMultiplesMenciones":
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMultiplesMenciones());
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El algoritmo de consenso especificado no existe");
        }
        coleccionesRepo.save(coleccion);
        return ResponseEntity.status(HttpStatus.OK).body("Se actualizó el algoritmo de consenso correctamente");
    }

    @Async
    @Scheduled(cron = "0 * * * * *") // cada hora
    public void refrescarColeccionesCronjob() {

        /*
        Specification<Hecho> specs1 = (root, query, cb) -> {
            if (query != null) query.distinct(true); // útil si después hay JOINs
            // activo = true AND atributosHecho.modificado = true (null => false)
            var activo = root.<Boolean>get("activo");
            var modif  = root.get("atributosHecho").<Boolean>get("modificado");
            return cb.and(cb.isTrue(activo), cb.isTrue(cb.coalesce(modif, cb.literal(false))));
        };*/

        List<Coleccion> colecciones = coleccionesRepo.findByActivoTrue();

        for(Coleccion coleccion : colecciones){
            Specification<Hecho> specs = crearSpecs(coleccion.getCriterios());

            Specification<Hecho> specFinal = Specification
                    .where(DISTINCT)
                    //.and(specs1)
                    .and(specs);

            List<Hecho> hechosFiltrados = hechoRepository.findAll(specFinal);
            coleccion.setModificado(false);
            coleccion.setHechos(hechosFiltrados);
            coleccionesRepo.save(coleccion);
        }
            hechoRepository.resetAllModificado();
    }

    private ResponseEntity<?> checkeoAdmin(Long id_usuario){
        Usuario usuario = usuariosRepo.findById(id_usuario).orElse(null);

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }
        return ResponseEntity.ok(usuario);
    }

    public ResponseEntity<?> refrescarColecciones(Long id_usuario){
        ResponseEntity<?> respuesta = this.checkeoAdmin(id_usuario);

        if (respuesta.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
            return respuesta;
        }
        this.refrescarColeccionesCronjob();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public void mapearHechosAColeccion(Coleccion coleccion){

        Specification<Hecho> specs = crearSpecs(coleccion.getCriterios());

        Specification<Hecho> specFinal = Specification
                .where(DISTINCT)
                .and(specs);

        coleccion.setHechos(hechoRepository.findAll(specFinal));

        coleccionesRepo.save(coleccion);
    }

    private Specification<Hecho> crearSpecs(List<Filtro> filtros) {
        return filtros.stream()
                .map(Filtro::toSpecification)   // o IFiltro::toSpecification
                .filter(Objects::nonNull)
                .reduce(Specification.where(null), Specification::and); // null-safe
    }

    private static final Specification<Hecho> DISTINCT = (root, query, cb) -> {
        query.distinct(true);
        return cb.conjunction();
    };

}

/*
* BIENVENIDO!!! LLEGASTE AL CONTENEDOR DE MIERDA
*
* public ResponseEntity<?> refrescarColecciones(Long idUsuario){
        Usuario usuario = usuariosRepo.findById(idUsuario).orElse(null);
        if (usuario!= null && !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }
        this.refrescarColeccionesCronjob();
        return ResponseEntity.status(HttpStatus.OK).body("Se refrescaron las colecciones correctamente");
    }

    private void actualizarHechosModificadosAColecciones(List<Coleccion> colecciones, List<Hecho> hechos){

        for (Coleccion coleccion: colecciones){
            List<Hecho> hechosColeccion = coleccion.getHechos();

            for (Hecho hecho: hechos){

                Long hechoId = hecho.getId();

                Hecho hechoEncontrado = hechosColeccion.stream()
                        .filter(h -> h.getId().equals(hechoId))
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
*
* */
