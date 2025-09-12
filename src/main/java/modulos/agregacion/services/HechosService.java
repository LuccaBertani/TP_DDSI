package modulos.agregacion.services;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.filtros.*;
import modulos.agregacion.repositories.*;
import modulos.agregacion.entities.fuentes.Dataset;
import modulos.buscadores.*;
import modulos.shared.dtos.input.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.agregacion.entities.fuentes.FuenteEstatica;
import modulos.agregacion.entities.usuario.Rol;
import modulos.agregacion.entities.usuario.Usuario;
import modulos.agregacion.entities.AtributosHecho;
import modulos.shared.dtos.input.ImportacionHechosInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.agregacion.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
    private final IHechoRepository hechoRepo;
    private final IPaisRepository repoPais;
    private final IProvinciaRepository repoProvincia;
    private final IHechoRepository hechoRepository;
    private final IUsuarioRepository usuariosRepo;
    private final IColeccionRepository coleccionRepo;
    private final IDatasetsRepository datasetsRepo;
    private final BuscadorCategoria buscadorCategoria;
    private final BuscadorPais buscadorPais;
    private final BuscadorProvincia buscadorProvincia;
    private final BuscadorHecho buscadorHecho;
    private final BuscadorFiltro buscadorFiltro;
    private final BuscadorUbicacion buscadorUbicacion;
    private final ICategoriaRepository categoriaRepository;
    private final ISinonimoRepository repoSinonimo;

    public HechosService(IHechosEstaticaRepository hechosEstaticaRepo,
                         IHechosDinamicaRepository hechosDinamicaRepo,
                         IUsuarioRepository usuariosRepo,
                         IColeccionRepository coleccionRepo,
                         IDatasetsRepository datasetsRepo,
                         BuscadorCategoria buscadorCategoria,
                         BuscadorPais buscadorPais,
                         BuscadorProvincia buscadorProvincia,
                         BuscadorHecho buscadorHecho,
                         IHechoRepository hechoRepository,
                         ICategoriaRepository categoriaRepository,
                         IProvinciaRepository repoProvincia,
                         IPaisRepository repoPais,
                         IHechoRepository hechoRepo,
                         BuscadorFiltro buscadorFiltro,
                         BuscadorUbicacion buscadorUbicacion, ISinonimoRepository repoSinonimo){
        this.repoProvincia = repoProvincia;
        this.repoPais = repoPais;
        this.hechosDinamicaRepo = hechosDinamicaRepo;
        this.hechosEstaticaRepo = hechosEstaticaRepo;
        this.usuariosRepo = usuariosRepo;
        this.coleccionRepo = coleccionRepo;
        this.datasetsRepo = datasetsRepo;
        this.buscadorCategoria = buscadorCategoria;
        this.buscadorPais = buscadorPais;
        this.buscadorProvincia = buscadorProvincia;
        this.buscadorHecho = buscadorHecho;
        this.hechoRepository = hechoRepository;
        this.categoriaRepository = categoriaRepository;
        this.hechoRepo = hechoRepo;
        this.buscadorFiltro = buscadorFiltro;
        this.buscadorUbicacion = buscadorUbicacion;
        this.repoSinonimo = repoSinonimo;
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


    //lo sube un administrador (lo considero carga dinamica)
    public ResponseEntity<?> subirHecho(SolicitudHechoInputDTO dtoInput) {

        if(dtoInput.getTitulo() == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Campos obligatorios no ingresados");
        }

        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario()).orElse(null);

        if (usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }

        usuario.incrementarHechosSubidos();

        HechoDinamica hecho = new HechoDinamica();

        AtributosHecho atributos = FormateadorHecho.formatearAtributosHecho(buscadorUbicacion, buscadorCategoria,buscadorPais, buscadorProvincia, dtoInput);

        hecho.setUsuario(usuario);
        hecho.setAtributosHecho(atributos);
        hecho.setActivo(true);
        hecho.getAtributosHecho().setModificado(true);
        hecho.getAtributosHecho().setFechaCarga(ZonedDateTime.now());
        hecho.getAtributosHecho().setFechaUltimaActualizacion(hecho.getAtributosHecho().getFechaCarga());
        hechosDinamicaRepo.save(hecho);

        return ResponseEntity.status(HttpStatus.CREATED).body("Se subió el hecho correctamente");
    }

    public ResponseEntity<?> importarHechos(ImportacionHechosInputDTO dtoInput, MultipartFile file) throws IOException {
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario()).orElse(null);

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
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

        List<HechoEstatica> hechos = fuente.leerFuente(buscadorUbicacion, buscadorCategoria, buscadorPais, buscadorProvincia, buscadorHecho);
        hechosEstaticaRepo.saveAll(hechos);

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

        List<Filtro> filtros = FormateadorHecho.obtenerListaDeFiltros(FormateadorHecho.formatearFiltrosColeccionDinamica(buscadorFiltro, buscadorCategoria,
                buscadorPais, buscadorProvincia, criterios));

        System.out.println("SIZE DE LA LISTA FILTROS: " + filtros.size());

        for(Filtro filtro : filtros){
            System.out.println(filtro.getClass());
            if(filtro instanceof FiltroProvincia){
                System.out.println("HOLA SOY UNA PROVINCIA MUY FELIZ!!: " + ((FiltroProvincia) filtro).getProvincia().getProvincia());
            }
        }

        Coleccion coleccion = coleccionRepo.findByIdAndActivoTrue(inputDTO.getId_coleccion()).orElse(null);

        if (coleccion == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No se encontró la colección");
        }

        Specification<Hecho> specColeccion =
                this.perteneceAColeccionYesConsensuadoSiAplica(coleccion.getId(),inputDTO.getNavegacionCurada());

        Specification<Hecho> specs = this.crearSpecs(filtros);

        Specification<Hecho> specFinal = Specification
                .where(DISTINCT)
                .and(specColeccion)
                .and(specs);

        List<Hecho> hechosFiltrados = hechoRepository.findAll(specFinal);

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

        Optional.ofNullable(hecho.getAtributosHecho().getUbicacion())
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
                });

        dto.setTitulo(hecho.getAtributosHecho().getTitulo());
        dto.setDescripcion(hecho.getAtributosHecho().getDescripcion());
        Optional.ofNullable(hecho.getAtributosHecho().getFechaAcontecimiento())
                .map(Object::toString)
                .ifPresent(dto::setFechaAcontecimiento);

        Optional.ofNullable(hecho.getAtributosHecho().getCategoria()).ifPresent(categoria -> {
            dto.setCategoria(categoria.getTitulo());
            dto.setId_categoria(categoria.getId());
        });
        return dto;
    }

    public ResponseEntity<?> getAllHechos() {
        List<Hecho> hechosTotales = hechoRepo.findAll();

        List<VisualizarHechosOutputDTO> outputDTO = hechosTotales.stream()
                .map(this::crearHechoDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
    }

    private Specification<Hecho> perteneceAColeccionYesConsensuadoSiAplica(Long coleccionId, boolean navegacionCurada) {
        if (coleccionId == null) {
            // Podés devolver cb.conjunction() para no filtrar nada; con null Spring ignora esta spec.
            return null;
        }

        return (root, query, cb) -> {
            query.distinct(true);

            // subquery que devuelve los IDs de Hecho que pertenecen a la colección
            Subquery<Long> sq = query.subquery(Long.class);
            Root<Coleccion> c = sq.from(Coleccion.class);

            // Elegimos la colección de hechos según el modo de navegación
            // - "hechosConsensuados": navegación curada
            // - "hechos": navegación irrestricta
            Join<Coleccion, Hecho> hJoin = navegacionCurada
                    ? c.join("hechosConsensuados", JoinType.INNER)
                    : c.join("hechos", JoinType.INNER);

            sq.select(hJoin.get("id"))
                    .where(cb.equal(c.get("id"), coleccionId));

            // h.id IN (subquery)
            return cb.in(root.get("id")).value(sq);
        };
    }

    public ResponseEntity<?> addCategoria(Long idUsuario, String categoriaStr, List<String> sinonimosString) {

        Usuario usuario = usuariosRepo.findById(idUsuario).orElse(null);
        if (usuario != null && usuario.getRol().equals(Rol.ADMINISTRADOR)){
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
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permiso para ejecutar esta acción");
    }

    public ResponseEntity<?> addSinonimoCategoria(Long idUsuario, Long idCategoria, String sinonimo_str) {

        ResponseEntity<?> respuesta = verificarDatos(idUsuario);

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

        ResponseEntity<?> respuesta = verificarDatos(idUsuario);

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

        ResponseEntity<?> respuesta = verificarDatos(idUsuario);

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

    private ResponseEntity<?> verificarDatos(Long idUsuario){
        Usuario usuario = usuariosRepo.findById(idUsuario).orElse(null);

        if(usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","El id de usuario no es valido"));
        }
        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","El usuario no tiene permiso para ejecutar esto"));
        }
        return ResponseEntity.ok().build();
    }

    private Specification<Hecho> crearSpecs(List<Filtro> filtros) {
        return filtros.stream()
                .map(Filtro::toSpecification)   // o IFiltro::toSpecification
                .filter(Objects::nonNull)
                .reduce(Specification.where(null), Specification::and); // NO meter distinct acá
    }

    private static final Specification<Hecho> DISTINCT = (root, query, cb) -> {
        query.distinct(true);
        return cb.conjunction();
    };

}
