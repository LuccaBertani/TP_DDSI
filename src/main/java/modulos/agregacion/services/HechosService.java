package modulos.agregacion.services;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbEstatica.HechoEstatica;
import modulos.agregacion.entities.DbMain.*;
import modulos.agregacion.entities.DbMain.filtros.*;
import modulos.agregacion.entities.DbMain.hechoRef.HechoRef;
import modulos.agregacion.entities.DbProxy.HechoProxy;
import modulos.agregacion.entities.HechoMemoria;
import modulos.agregacion.repositories.DbDinamica.IHechosDinamicaRepository;
import modulos.agregacion.repositories.DbEstatica.IDatasetsRepository;
import modulos.agregacion.repositories.DbEstatica.IHechosEstaticaRepository;
import modulos.agregacion.repositories.DbMain.*;
import modulos.agregacion.repositories.DbProxy.IHechosProxyRepository;
import modulos.shared.utils.FormateadorHecho;
import modulos.agregacion.entities.DbEstatica.Dataset;
import modulos.buscadores.*;
import modulos.shared.dtos.input.*;
import modulos.shared.utils.FormateadorHechoMemoria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.agregacion.entities.fuentes.FuenteEstatica;
import modulos.agregacion.entities.DbMain.usuario.Rol;
import modulos.agregacion.entities.DbMain.usuario.Usuario;
import modulos.agregacion.entities.atributosHecho.AtributosHecho;
import modulos.shared.dtos.input.ImportacionHechosInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class HechosService {


    private final IHechosEstaticaRepository hechosEstaticaRepo;
    private final IHechosDinamicaRepository hechosDinamicaRepo;
    private final IHechosProxyRepository hechosProxyRepo;
    private final IPaisRepository repoPais;
    private final IProvinciaRepository repoProvincia;
    private final IUsuarioRepository usuariosRepo;
    private final IColeccionRepository coleccionRepo;
    private final IDatasetsRepository datasetsRepo;
    private final BuscadoresRegistry buscadores;
    private final ICategoriaRepository categoriaRepository;
    private final ISinonimoRepository repoSinonimo;
    private final FormateadorHechoMemoria formateadorHechoMemoria;

    public HechosService(IHechosEstaticaRepository hechosEstaticaRepo,
                         IHechosDinamicaRepository hechosDinamicaRepo,
                         IHechosProxyRepository hechosProxyRepo,
                         IUsuarioRepository usuariosRepo,
                         IColeccionRepository coleccionRepo,
                         IDatasetsRepository datasetsRepo,
                         ICategoriaRepository categoriaRepository,
                         IProvinciaRepository repoProvincia,
                         IPaisRepository repoPais,
                        BuscadoresRegistry buscadores, ISinonimoRepository repoSinonimo,
                         FormateadorHechoMemoria formateadorHechoMemoria){
        this.repoProvincia = repoProvincia;
        this.repoPais = repoPais;
        this.hechosDinamicaRepo = hechosDinamicaRepo;
        this.hechosEstaticaRepo = hechosEstaticaRepo;
        this.hechosProxyRepo = hechosProxyRepo;
        this.usuariosRepo = usuariosRepo;
        this.coleccionRepo = coleccionRepo;
        this.datasetsRepo = datasetsRepo;
        this.categoriaRepository = categoriaRepository;
        this.repoSinonimo = repoSinonimo;
        this.buscadores = buscadores;
        this.formateadorHechoMemoria = formateadorHechoMemoria;
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

    private ResponseEntity<?> checkeoAdmin(Long id_usuario){

        if (id_usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Usuario usuario = usuariosRepo.findById(id_usuario).orElse(null);

        if (usuario == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el usuario");
        }
        else if (!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(usuario);
    }


    //lo sube un administrador (lo considero carga dinamica)
    @Transactional
    public ResponseEntity<?> subirHecho(SolicitudHechoInputDTO dtoInput) {

        if(dtoInput.getTitulo() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ResponseEntity<?> rta = checkeoAdmin(dtoInput.getId_usuario());

        if (!rta.getStatusCode().equals(HttpStatus.OK)){
            return rta;
        }

        Usuario usuario = (Usuario)rta.getBody();
        usuario.incrementarHechosSubidos();

        HechoDinamica hecho = new HechoDinamica();

        System.out.println("SOY UN ID PAIS CONTENTO: " + dtoInput.getId_pais());
        System.out.println("SOY UN ID PROVINCIA CONTENTO: " + dtoInput.getId_provincia());
        AtributosHecho atributos = FormateadorHecho.formatearAtributosHecho(buscadores, dtoInput);

        hecho.setUsuario_id(usuario.getId());
        hecho.setAtributosHecho(atributos);
        hecho.setActivo(true);
        hecho.getAtributosHecho().setModificado(true);
        hecho.getAtributosHecho().setFuente(Fuente.DINAMICA);
        hecho.getAtributosHecho().setFechaCarga(ZonedDateTime.now());
        hecho.getAtributosHecho().setFechaUltimaActualizacion(hecho.getAtributosHecho().getFechaCarga());
        hechosDinamicaRepo.save(hecho);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    public ResponseEntity<?> importarHechos(ImportacionHechosInputDTO dtoInput, MultipartFile file) throws IOException {
        ResponseEntity<?> rta = checkeoAdmin(dtoInput.getId_usuario());

        if (!rta.getStatusCode().equals(HttpStatus.OK)){
            return rta;
        }

        // 1) Validaciones básicas
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Debés adjuntar un archivo CSV");
        }
        if (!Objects.equals(file.getContentType(), "text/csv") && !Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("El archivo debe ser CSV");
        }

        // === Opción A: guardar en disco ===
        Path base = Paths.get("uploads/datasets").toAbsolutePath().normalize();
        Files.createDirectories(base);
        String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destino = base.resolve(storedName);
        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);


        FuenteEstatica fuente = new FuenteEstatica();
        Dataset dataset = new Dataset(dtoInput.getFuenteString());
        dataset.setStoragePath(destino.toString());
        datasetsRepo.save(dataset);
        fuente.setDataSet(dataset);

        List<HechoEstatica> hechos = fuente.leerFuente((Usuario)rta.getBody(),buscadores);

        for (HechoEstatica hecho : hechos){
            hecho.setActivo(true);
            ZonedDateTime fechaActual = ZonedDateTime.now();
            hecho.getAtributosHecho().setFechaCarga(fechaActual);
            hecho.getAtributosHecho().setFechaUltimaActualizacion(fechaActual);
            hechosEstaticaRepo.save(hecho);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Se importaron los hechos correctamente");
    }


    public ResponseEntity<?> getHechosColeccion(GetHechosColeccionInputDTO inputDTO){

        CriteriosColeccionDTO criterios = new CriteriosColeccionDTO(
                inputDTO.getCategoriaId(),
                inputDTO.getContenidoMultimedia(),
                inputDTO.getDescripcion(),
                inputDTO.getFechaAcontecimientoInicial(),
                inputDTO.getFechaAcontecimientoFinal(),
                inputDTO.getFechaCargaInicial(),
                inputDTO.getFechaCargaFinal(),
                inputDTO.getOrigen(),
                inputDTO.getPaisId(),
                inputDTO.getTitulo(),
                inputDTO.getProvinciaId());

        System.out.println(inputDTO.getCategoriaId());

        List<Filtro> filtros = FormateadorHecho.obtenerListaDeFiltros(FormateadorHecho.formatearFiltrosColeccionDinamica(buscadores, criterios));

        Coleccion coleccion = coleccionRepo.findByIdAndActivoTrue(inputDTO.getId_coleccion()).orElse(null);

        if (coleccion == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la colección");
        }

        List<Long> hechosIds = new ArrayList<>(coleccion.getHechosConsensuados().stream().map(h -> h.getKey().getId()).toList());

        Specification<HechoEstatica> specColeccionEstatica =
                this.perteneceAColeccionYesConsensuadoSiAplica(hechosIds, HechoEstatica.class);
        Specification<HechoDinamica> specColeccionDinamica =
                this.perteneceAColeccionYesConsensuadoSiAplica(hechosIds, HechoDinamica.class);
        Specification<HechoProxy> specColeccionProxy =
                this.perteneceAColeccionYesConsensuadoSiAplica(hechosIds, HechoProxy.class);

        Specification<HechoEstatica> specsEstatica = this.crearSpecs(filtros, HechoEstatica.class);
        Specification<HechoDinamica> specsDinamica = this.crearSpecs(filtros, HechoDinamica.class);
        Specification<HechoProxy> specsProxy = this.crearSpecs(filtros, HechoProxy.class);

        Specification<HechoEstatica> specFinalEstatica = Specification
                .where(this.distinct(HechoEstatica.class))
                .and(specColeccionEstatica)
                .and(specsEstatica);

        Specification<HechoDinamica> specFinalDinamica = Specification
                .where(this.distinct(HechoDinamica.class))
                .and(specColeccionDinamica)
                .and(specsDinamica);

        Specification<HechoProxy> specFinalProxy = Specification
                .where(this.distinct(HechoProxy.class))
                .and(specColeccionProxy)
                .and(specsProxy);

        List<HechoEstatica> hechosFiltradosEstatica = hechosEstaticaRepo.findAll(specFinalEstatica);
        List<HechoDinamica> hechosFiltradosDinamica = hechosDinamicaRepo.findAll(specFinalDinamica);
        List<HechoProxy> hechosFiltradosProxy = hechosProxyRepo.findAll(specFinalProxy);

        List<Hecho> hechosFiltrados = new ArrayList<>();
        hechosFiltrados.addAll(hechosFiltradosEstatica);
        hechosFiltrados.addAll(hechosFiltradosDinamica);
        hechosFiltrados.addAll(hechosFiltradosProxy);

        if(hechosFiltrados.isEmpty()){
            System.out.println("soy un estorbo");
        }

        List<VisualizarHechosOutputDTO> outputDTO = hechosFiltrados.stream()
                .map(this::crearHechoDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
    }

    private VisualizarHechosOutputDTO crearHechoDto(Hecho hecho){
        VisualizarHechosOutputDTO dto = new VisualizarHechosOutputDTO();
        dto.setId(hecho.getId());
        dto.setFuente(hecho.getAtributosHecho().getFuente().codigoEnString());
        HechoMemoria hechoMemoria = formateadorHechoMemoria.formatearHechoMemoria(hecho);

        Optional.ofNullable(hechoMemoria.getAtributosHecho().getUbicacion())
                .ifPresent(ubicacion -> {
                    Optional.ofNullable(ubicacion.getPais())
                            .ifPresent(pais -> {
                                dto.setPais(pais.getPais());
                                dto.setId_pais(pais.getId());
                            });
                    Optional.ofNullable(ubicacion.getProvincia())
                            .ifPresent(provincia -> {
                                dto.setProvincia(provincia.getProvincia());
                                dto.setId_provincia(provincia.getId());
                            });
                    // TODO DEBERIA haber una ubicacion seteada para la latitud y la longitud
                    Optional.ofNullable(hecho.getAtributosHecho().getLatitud())
                            .ifPresent(dto::setLatitud);
                    Optional.ofNullable(hecho.getAtributosHecho().getLongitud())
                            .ifPresent(dto::setLongitud);
                });

        dto.setTitulo(hecho.getAtributosHecho().getTitulo());
        dto.setDescripcion(hecho.getAtributosHecho().getDescripcion());
        Optional.ofNullable(hecho.getAtributosHecho().getFechaAcontecimiento())
                .map(Object::toString)
                .ifPresent(dto::setFechaAcontecimiento);

        Optional.ofNullable(hechoMemoria.getAtributosHecho().getCategoria()).ifPresent(categoria -> {
            dto.setCategoria(categoria.getTitulo());
            dto.setId_categoria(categoria.getId());
        });
        return dto;
    }

    public ResponseEntity<?> getAllHechos() {


        List<Hecho> hechosTotales = new ArrayList<>();
        hechosTotales.addAll(hechosEstaticaRepo.findAll());
        hechosTotales.addAll(hechosDinamicaRepo.findAll());
        hechosTotales.addAll(hechosProxyRepo.findAll());

        List<VisualizarHechosOutputDTO> outputDTO = hechosTotales.stream()
                .map(this::crearHechoDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
    }

    private <T> Specification<T> perteneceAColeccionYesConsensuadoSiAplica(List<Long> idsHechosDeColeccionYConsensuados, Class<T> clazz) {
        if (idsHechosDeColeccionYConsensuados == null || idsHechosDeColeccionYConsensuados.isEmpty()) {
            // devuelve false siempre -> WHERE 1=0
            return (root, query, cb) -> cb.disjunction();
        }

        return (root, query, cb) -> root.get("id").in(idsHechosDeColeccionYConsensuados);
    }

    public ResponseEntity<?> addCategoria(Long idUsuario, String categoriaStr, List<String> sinonimosString) {

        ResponseEntity<?> rta = checkeoAdmin(idUsuario);

        if (!rta.getStatusCode().equals(HttpStatus.OK)){
            return rta;
        }
        Categoria categoria = new Categoria();
        categoria.setTitulo(categoriaStr);
        List<Sinonimo> sinonimos = new ArrayList<>();
        if (sinonimosString!=null && !sinonimosString.isEmpty()){
            for (String sinonimo: sinonimosString){
                sinonimos.add(new Sinonimo(sinonimo));
            }
        }
        categoria.setSinonimos(sinonimos);
        categoriaRepository.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body("Se creó la categoría correctamente");
    }

    public ResponseEntity<?> addSinonimoCategoria(Long idUsuario, Long idCategoria, String sinonimo_str) {

        ResponseEntity<?> respuesta = checkeoAdmin(idUsuario);

        if (!respuesta.getStatusCode().equals(HttpStatus.OK)){
            return respuesta;
        }

    Categoria categoria = categoriaRepository.findById(idCategoria).orElse(null);

        if(categoria == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","El id de la categoria no es valido"));
        }

        Sinonimo sinonimo = repoSinonimo.findByIdCategoriaAndNombre(idCategoria, sinonimo_str).orElse(null);

        if(sinonimo != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El sinonimo ya existe");
        }

        sinonimo = new Sinonimo(sinonimo_str);

        categoria.getSinonimos().add(sinonimo);

        categoriaRepository.save(categoria);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<?> addSinonimoPais(Long idUsuario, Long idPais, String sinonimo_str) {

        ResponseEntity<?> respuesta = checkeoAdmin(idUsuario);

        if (!respuesta.getStatusCode().equals(HttpStatus.OK)){
            return respuesta;
        }

        Pais pais = repoPais.findById(idPais).orElse(null);

        if(pais == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","El id del pais no es valido"));
        }

        Sinonimo sinonimo = repoSinonimo.findByIdPaisAndNombre(idPais, sinonimo_str).orElse(null);

        if(sinonimo != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El sinonimo ya existe");
        }

        sinonimo = new Sinonimo(sinonimo_str);

        pais.getSinonimos().add(sinonimo);

        repoPais.save(pais);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<?> addSinonimoProvincia(Long idUsuario, Long idProvincia, String sinonimo_str) {

        ResponseEntity<?> respuesta = checkeoAdmin(idUsuario);

        if (!respuesta.getStatusCode().equals(HttpStatus.OK)){
            return respuesta;
        }

        Provincia provincia = repoProvincia.findById(idProvincia).orElse(null);

        if(provincia == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","El id de la provincia no es valido"));
        }

        Sinonimo sinonimo = repoSinonimo.findByIdProvinciaAndNombre(idProvincia, sinonimo_str).orElse(null);

        if(sinonimo != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El sinonimo ya existe");
        }

        sinonimo = new Sinonimo(sinonimo_str);

        provincia.getSinonimos().add(sinonimo);

        repoProvincia.save(provincia);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private <T> Specification<T> crearSpecs(List<Filtro> filtros, Class<T> clazz) {
        return filtros.stream()
                .map(filtro->filtro.toSpecification(clazz))  // o IFiltro::toSpecification
                .filter(Objects::nonNull)
                .reduce(Specification.where(null), Specification::and); // NO meter distinct acá
    }

    private <T> Specification<T> distinct(Class <T> clazz) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.conjunction(); // no agrega condición extra
        };
    }

}
