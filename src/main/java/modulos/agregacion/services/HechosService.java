package modulos.agregacion.services;

import com.sun.net.httpserver.HttpsServer;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.Filtrador;
import modulos.agregacion.entities.filtros.*;
import modulos.agregacion.repositories.*;
import modulos.agregacion.entities.fuentes.Dataset;
import modulos.agregacion.entities.fuentes.Origen;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorHecho;
import modulos.buscadores.BuscadorPais;
import modulos.buscadores.BuscadorProvincia;
import modulos.shared.dtos.input.*;
import modulos.agregacion.entities.RespuestaHttp;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.agregacion.entities.fuentes.FuenteEstatica;
import modulos.agregacion.entities.usuario.Rol;
import modulos.agregacion.entities.usuario.Usuario;
import modulos.agregacion.entities.AtributosHecho;
import modulos.shared.dtos.input.ImportacionHechosInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.agregacion.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
public class HechosService {


    private final IHechosProxyRepository hechosProxyRepo;
    private final IHechosEstaticaRepository hechosEstaticaRepo;
    private final IHechosDinamicaRepository hechosDinamicaRepo;
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

    private final ICategoriaRepository categoriaRepository;

    public HechosService(IHechosProxyRepository hechosProxyRepo,
                         IHechosEstaticaRepository hechosEstaticaRepo,
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
                         IPaisRepository repoPais){
        this.repoProvincia = repoProvincia;
        this.repoPais = repoPais;
        this.hechosProxyRepo = hechosProxyRepo;
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
    }

    /* Se pide que, una vez por hora, el servicio de agregación actualice los hechos pertenecientes a las distintas colecciones,
     en caso de que las fuentes hayan incorporado nuevos hechos.*/
    @Async
    @Scheduled(cron = "0 0 * * * *")
    public void actualizarColeccionesCronjob(){
        List<Coleccion> colecciones = coleccionRepo.findAll();
        for(Coleccion coleccion: colecciones){
            List<Hecho> hechosFiltrados = Filtrador.aplicarFiltros(coleccion.getCriterios(),coleccion.getHechos());
            coleccion.setHechos(hechosFiltrados);
            coleccionRepo.save(coleccion);
        }
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

    @Async
    @Scheduled(cron = "0 0 * * * *") // cada hora
    public void refrescarColeccionesCronjob() {

        List<HechoEstatica> hechosEstatica = hechosEstaticaRepo.findAll();
        List<HechoDinamica> hechosDinamica = hechosDinamicaRepo.findAll();

        List<Hecho> hechos = Stream.concat(hechosEstatica.stream(), hechosDinamica.stream())
                .toList();

        List<Coleccion> colecciones = coleccionRepo.findAll();

        List<Hecho> hechosModificados = hechos.stream().filter(
                hecho->hecho.getAtributosHecho().getModificado().equals(true)
                && hecho.getActivo().equals(true))
                .toList();

        List<Coleccion> coleccionesModificadas = colecciones.stream().filter(
                coleccion -> coleccion.getModificado().equals(true)
                && coleccion.getActivo().equals(true))
                .toList();

        if (!hechosModificados.isEmpty()){
            this.eliminarHechosModificadosDeColecciones(colecciones, hechosModificados);
            this.mapearHechosAColecciones(colecciones,hechosModificados);
        }
        if (!coleccionesModificadas.isEmpty()){
            this.mapearHechosAColecciones(coleccionesModificadas, hechos);
        }
        this.setearFalseModificado(hechosModificados,coleccionesModificadas);
    }

    public ResponseEntity<?> refrescarColecciones(Long idUsuario){
        Usuario usuario = usuariosRepo.findById(idUsuario).orElse(null);
        if (usuario!= null && !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }
        this.refrescarColeccionesCronjob();
        return ResponseEntity.status(HttpStatus.OK).body("Se refrescaron las colecciones correctamente");
    }

    private void eliminarHechosModificadosDeColecciones(List<Coleccion> colecciones, List<Hecho> hechos){

        for (Coleccion coleccion: colecciones){
            List<Hecho> hechosColeccion = coleccion.getHechos();

            for (Hecho hecho: hechos){

                Long hechoId = hecho.getId();
                Origen hechoOrigen = hecho.getAtributosHecho().getOrigen();

                Hecho hechoEncontrado = hechosColeccion.stream()
                                        .filter(h -> h.getId().equals(hechoId) && h.getAtributosHecho().getOrigen().equals(hechoOrigen))
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
    public ResponseEntity<?> subirHecho(SolicitudHechoInputDTO dtoInput) {

        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario()).orElse(null);

        if (usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }

        HechoDinamica hecho = new HechoDinamica();

        AtributosHecho atributos = FormateadorHecho.formatearAtributosHecho(buscadorCategoria,buscadorPais, buscadorProvincia, dtoInput);

        hecho.setAtributosHecho(atributos);
        hecho.setActivo(true);
        hecho.getAtributosHecho().setFechaCarga(ZonedDateTime.now());
        hecho.getAtributosHecho().setFechaUltimaActualizacion(hecho.getAtributosHecho().getFechaCarga());
        hechosDinamicaRepo.save(hecho);

        return ResponseEntity.status(HttpStatus.CREATED).body("Se subió el hecho correctamente");
    }

    public ResponseEntity<?> importarHechos(ImportacionHechosInputDTO dtoInput){
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario()).orElse(null);

        if (usuario == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el usuario");
        }

        if (!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tenés permisos para ejecutar esta acción");
        }

        FuenteEstatica fuente = new FuenteEstatica();
        Dataset dataset = new Dataset(dtoInput.getFuenteString());
        datasetsRepo.save(dataset);
        fuente.setDataSet(dataset);

        List<HechoEstatica> hechos = fuente.leerFuente(buscadorCategoria, buscadorPais, buscadorProvincia, buscadorHecho);
        hechosEstaticaRepo.saveAll(hechos);

        return ResponseEntity.status(HttpStatus.CREATED).body("Se importaron los hechos correctamente");
    }


    public ResponseEntity<?> getHechosColeccion(GetHechosColeccionInputDTO inputDTO){

        List<Filtro> filtros = FormateadorHecho.obtenerListaDeFiltros(FormateadorHecho.formatearFiltrosColeccion(buscadorCategoria, buscadorPais, buscadorProvincia, new CriteriosColeccionDTO(
                inputDTO.getCategoria(),
                inputDTO.getContenidoMultimedia(),
                inputDTO.getDescripcion(),
                inputDTO.getFechaAcontecimientoInicial(),
                inputDTO.getFechaAcontecimientoFinal(),
                inputDTO.getFechaCargaInicial(),
                inputDTO.getFechaCargaFinal(),
                inputDTO.getOrigen(),
                inputDTO.getPais(),
                inputDTO.getTitulo()
        )));

        Coleccion coleccion = coleccionRepo.findById(inputDTO.getId_coleccion()).orElse(null);

        if (coleccion == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No se encontró la colección");
        }


        Specification<Hecho> specColeccion =
                this.perteneceAColeccionYesConsensuado(coleccion.getId());

        Specification<Hecho> specAnd = filtros.stream()
                .map(IFiltro::toSpecification)
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction()); // neutro (1=1)

// Forzar DISTINCT una sola vez para evitar duplicados cuando hay JOINs
        Specification<Hecho> distinct = (root, query, cb) -> {
            query.distinct(true);
            return cb.conjunction();
        };

// Spec final = DISTINCT AND (AND de filtros)
        Specification<Hecho> specFinal = distinct
                .and(specColeccion == null ? (r,q,cb) -> cb.conjunction() : specColeccion)
                .and(specAnd);
// Ejecutar
        List<Hecho> hechosFiltrados = hechoRepository.findAll(specFinal);

        //parseo al dto
        List<VisualizarHechosOutputDTO> outputDTO = hechosFiltrados.stream().map(hecho -> {
            VisualizarHechosOutputDTO dto = new VisualizarHechosOutputDTO();
            dto.setId(hecho.getId());
            dto.setPais(hecho.getAtributosHecho().getUbicacion().getPais().getPais());
            dto.setProvincia(hecho.getAtributosHecho().getUbicacion().getProvincia().getProvincia());
            dto.setTitulo(hecho.getAtributosHecho().getTitulo());
            dto.setDescripcion(hecho.getAtributosHecho().getDescripcion());
            dto.setFechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento().toString());
            dto.setCategoria(hecho.getAtributosHecho().getCategoria().getTitulo());
            return dto;
        }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
    }

    public ResponseEntity<?> getAllHechos() {
        List<Hecho> hechosTotales = new ArrayList<>();
        hechosTotales.addAll(hechosDinamicaRepo.findAll());
        hechosTotales.addAll(hechosProxyRepo.findAll());
        hechosTotales.addAll(hechosEstaticaRepo.findAll());

        List<VisualizarHechosOutputDTO> outputDTO = hechosTotales.stream().map(hecho -> {
            VisualizarHechosOutputDTO dto = new VisualizarHechosOutputDTO();
            dto.setId(hecho.getId());
            dto.setPais(hecho.getAtributosHecho().getUbicacion().getPais().getPais());
            dto.setProvincia(hecho.getAtributosHecho().getUbicacion().getProvincia().getProvincia());
            dto.setTitulo(hecho.getAtributosHecho().getTitulo());
            dto.setDescripcion(hecho.getAtributosHecho().getDescripcion());
            dto.setFechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento().toString());
            dto.setCategoria(hecho.getAtributosHecho().getCategoria().getTitulo());
            return dto;
        }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
    }

    private Specification<Hecho> perteneceAColeccionYesConsensuado(Long coleccionId) {
        if (coleccionId == null) return null;

        return (root, query, cb) -> {
            query.distinct(true);

            Subquery<Long> sq = query.subquery(Long.class);
            Root<Coleccion> coleccionRoot = sq.from(Coleccion.class);
            Join<Coleccion, Hecho> hechosJoin = coleccionRoot.join("hechosConsensuados", JoinType.INNER);

            sq.select(hechosJoin.get("id"))
                    .where(cb.equal(coleccionRoot.get("id"), coleccionId));

            return cb.in(root.get("id")).value(sq); // h.id IN (subquery)
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

        Sinonimo sinonimo = new Sinonimo(sinonimo_str);

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

        Sinonimo sinonimo = new Sinonimo(sinonimo_str);

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

        Sinonimo sinonimo = new Sinonimo(sinonimo_str);

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


}
