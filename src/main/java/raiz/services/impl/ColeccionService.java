package raiz.services.impl;

import raiz.models.dtos.input.ColeccionInputDTO;
import raiz.models.dtos.input.ColeccionUpdateInputDTO;
import raiz.models.dtos.input.FiltroHechosDTO;
import raiz.models.dtos.output.ColeccionOutputDTO;
import raiz.models.entities.*;
import raiz.models.entities.algoritmosConsenso.IAlgoritmoConsenso;
import raiz.models.entities.algoritmosConsenso.impl.AlgoritmoConsensoMayoriaAbsoluta;
import raiz.models.entities.algoritmosConsenso.impl.AlgoritmoConsensoMayoriaSimple;
import raiz.models.entities.algoritmosConsenso.impl.AlgoritmoConsensoMultiplesMenciones;
import raiz.models.entities.buscadores.BuscadorCategoria;
import raiz.models.entities.buscadores.BuscadorPais;
import raiz.models.entities.filtros.*;
import raiz.models.entities.fuentes.FuenteEstatica;
import raiz.models.entities.personas.Rol;
import raiz.models.entities.personas.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import raiz.models.repositories.*;
import raiz.services.IColeccionService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColeccionService implements IColeccionService {

    private final IHechosProxyRepository hechosProxyRepo;
    private final IHechosEstaticaRepository hechosEstaticaRepo;
    private final IHechosDinamicaRepository hechosDinamicaRepo;
    private final IColeccionRepository coleccionesRepo;
    private final IUsuarioRepository usuariosRepo;

    public ColeccionService(IHechosProxyRepository hechosProxyRepo, IHechosEstaticaRepository hechosEstaticaRepo, IHechosDinamicaRepository hechosDinamicaRepo, IColeccionRepository coleccionesRepo, IUsuarioRepository usuariosRepo) {
        this.hechosProxyRepo = hechosProxyRepo;
        this.hechosEstaticaRepo = hechosEstaticaRepo;
        this.hechosDinamicaRepo = hechosDinamicaRepo;
        this.coleccionesRepo = coleccionesRepo;
        this.usuariosRepo = usuariosRepo;
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

    @Override
    public RespuestaHttp<Void> crearColeccion(ColeccionInputDTO dtoInput) {

        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());

        if (usuario == null || !usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return new RespuestaHttp<>(null, HttpStatus.UNAUTHORIZED.value());
        }

        DatosColeccion datosColeccion = new DatosColeccion(dtoInput.getTitulo(), dtoInput.getDescripcion());

        Coleccion coleccion = new Coleccion(datosColeccion,coleccionesRepo.getProxId());

        List<Filtro> criterios = new ArrayList<>();

        Filtro filtroCategoria = null;
        Filtro filtroFechaCarga = null;
        Filtro filtroFechaAcontecimiento = null;
        Filtro filtroPais = null;
        Filtro filtroContenidoMultimedia = null;
        Filtro filtroDescripcion = null;
        Filtro filtroOrigen = null;
        Filtro filtroTitulo = null;

        IAlgoritmoConsenso algoritmoConsenso = null;

        if(dtoInput.getCategoria() != null){
            Categoria categoria = BuscadorCategoria.buscarOCrear(hechosDinamicaRepo.findAll(),dtoInput.getCategoria(),hechosProxyRepo.findAll(),hechosEstaticaRepo.findAll());
            filtroCategoria = new FiltroCategoria(categoria);
        }
        criterios.add(filtroCategoria);

        if (dtoInput.getFechaCargaInicial()!=null && dtoInput.getFechaCargaFinal() !=null){
            filtroFechaCarga = new FiltroFechaAcontecimiento(FechaParser.parsearFecha(dtoInput.getFechaCargaInicial()), FechaParser.parsearFecha(dtoInput.getFechaCargaFinal()));
        }
        criterios.add(filtroFechaCarga);

        if(dtoInput.getFechaAcontecimientoFinal() != null && dtoInput.getFechaAcontecimientoInicial() != null) {
            filtroFechaAcontecimiento = new FiltroFechaAcontecimiento(FechaParser.parsearFecha(dtoInput.getFechaAcontecimientoInicial()),FechaParser.parsearFecha(dtoInput.getFechaAcontecimientoFinal()));
        }
        criterios.add(filtroFechaAcontecimiento);
        if(dtoInput.getPais() != null) {
            Pais pais = BuscadorPais.buscarOCrear(hechosDinamicaRepo.findAll(),dtoInput.getPais(),hechosProxyRepo.findAll(),hechosEstaticaRepo.findAll());
            filtroPais = new FiltroPais(pais);
        }
        criterios.add(filtroPais);
        if(dtoInput.getContenidoMultimedia() != null) {
            filtroContenidoMultimedia = new FiltroContenidoMultimedia(TipoContenido.fromCodigo(dtoInput.getContenidoMultimedia()));
        }
        criterios.add(filtroContenidoMultimedia);
        if(dtoInput.getDescripcion() != null){
            filtroDescripcion = new FiltroDescripcion(dtoInput.getDescripcion());
        }
        criterios.add(filtroDescripcion);
        if(dtoInput.getOrigen() != null){
            filtroOrigen = new FiltroOrigen(Origen.fromCodigo(dtoInput.getOrigen()));
        }
        criterios.add(filtroOrigen);
        if(dtoInput.getTituloFiltro() != null){
            filtroTitulo = new FiltroTitulo(dtoInput.getTituloFiltro());
        }
        criterios.add(filtroTitulo);
        if (dtoInput.getAlgoritmoConsenso() != null){
            if (dtoInput.getAlgoritmoConsenso().equals("mayoria-absoluta"))
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaAbsoluta(coleccion));
            else if (dtoInput.getAlgoritmoConsenso().equals("mayoria-simple"))
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaSimple(coleccion));
            else if (dtoInput.getAlgoritmoConsenso().equals("multiples-menciones"))
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMultiplesMenciones(coleccion));
        }

        coleccion.addCriterios(criterios);
        List<Hecho> hechosDinamica = hechosDinamicaRepo.findAll();
        List<Hecho> hechosEstatica = hechosEstaticaRepo.findAll();
        List<Hecho> hechosProxy = hechosProxyRepo.findAll();

        coleccion.addHechos(Filtrador.aplicarFiltros(criterios, hechosDinamica));
        coleccion.addHechos(Filtrador.aplicarFiltros(criterios, hechosEstatica));
        coleccion.addHechos(Filtrador.aplicarFiltros(criterios, hechosProxy));

        coleccionesRepo.save(coleccion);

        return new RespuestaHttp<>(null, HttpStatus.CREATED.value());

    }
//TODO El orden fijo global para guardar criterios en la lista de criterios de la coleccion es [filtroCategoria,filtroFechaCarga,filtroFechaAcontecimiento,filtroPais,filtroContenidoMultimedia,FiltroDescripcion,FiltroOrigen,FiltroTitulo]
    @Override
    public RespuestaHttp<List<ColeccionOutputDTO>> obtenerTodasLasColecciones(){
        List<ColeccionOutputDTO> listaDTO = new ArrayList<>();

        List<Coleccion> colecciones = coleccionesRepo.findAll();

        for (Coleccion coleccion : colecciones){
            ColeccionOutputDTO dto = new ColeccionOutputDTO();
            dto.setId(coleccion.getId());
            dto.setNombre(coleccion.getTitulo());
            dto.setDescripcion(coleccion.getDescripcion());
            FiltroHechosDTO filtroHechosDTO = new FiltroHechosDTO();

            FiltroCategoria filtroCategoria = (FiltroCategoria)coleccion.getCriterio().get(0);
            filtroHechosDTO.setCategoria(filtroCategoria.getCategoria().getTitulo());

            FiltroFechaCarga filtroFechaCarga = (FiltroFechaCarga)coleccion.getCriterio().get(1);
            filtroHechosDTO.setFechaCargaFinal(filtroFechaCarga.getFechaInicial().toString());
            filtroHechosDTO.setFechaCargaFinal(filtroFechaCarga.getFechaFinal().toString());

            FiltroFechaAcontecimiento filtroFechaAcontecimiento = (FiltroFechaAcontecimiento)coleccion.getCriterio().get(2);
            filtroHechosDTO.setFechaAcontecimientoInicial(filtroFechaAcontecimiento.getFechaInicial().toString());
            filtroHechosDTO.setFechaAcontecimientoFinal(filtroFechaAcontecimiento.getFechaFinal().toString());

            FiltroPais filtroPais = (FiltroPais)coleccion.getCriterio().get(3);
            filtroHechosDTO.setPais(filtroPais.getPais().getPais());

            dto.setFiltros(filtroHechosDTO);
            listaDTO.add(dto);
        }
        return new RespuestaHttp<>(listaDTO, HttpStatus.OK.value());
    }

    @Override
    public RespuestaHttp<ColeccionOutputDTO> getColeccion(Long id_coleccion) {

        Coleccion coleccion = coleccionesRepo.findById(id_coleccion);

        if(coleccion == null){
            return new RespuestaHttp<>(null,HttpStatus.NO_CONTENT.value());
        }

        ColeccionOutputDTO dto = new ColeccionOutputDTO();

        dto.setId(coleccion.getId());
        dto.setNombre(coleccion.getTitulo());
        dto.setDescripcion(coleccion.getDescripcion());
        FiltroHechosDTO filtroHechosDTO = new FiltroHechosDTO();

        FiltroCategoria filtroCategoria = (FiltroCategoria)coleccion.getCriterio().get(0);
        filtroHechosDTO.setCategoria(filtroCategoria.getCategoria().getTitulo());

        FiltroFechaCarga filtroFechaCarga = (FiltroFechaCarga)coleccion.getCriterio().get(1);
        filtroHechosDTO.setFechaCargaFinal(filtroFechaCarga.getFechaInicial().toString());
        filtroHechosDTO.setFechaCargaFinal(filtroFechaCarga.getFechaFinal().toString());

        FiltroFechaAcontecimiento filtroFechaAcontecimiento = (FiltroFechaAcontecimiento)coleccion.getCriterio().get(2);
        filtroHechosDTO.setFechaAcontecimientoInicial(filtroFechaAcontecimiento.getFechaInicial().toString());
        filtroHechosDTO.setFechaAcontecimientoFinal(filtroFechaAcontecimiento.getFechaFinal().toString());

        FiltroPais filtroPais = (FiltroPais)coleccion.getCriterio().get(3);
        filtroHechosDTO.setPais(filtroPais.getPais().getPais());

        dto.setFiltros(filtroHechosDTO);

        return new RespuestaHttp<>(dto, HttpStatus.OK.value());

    }

    @Override
    public RespuestaHttp<ColeccionOutputDTO> deleteColeccion(Long id_coleccion) {
        Coleccion coleccion = coleccionesRepo.findById(id_coleccion);
        if(coleccion == null){
            return new RespuestaHttp<>(null,HttpStatus.NO_CONTENT.value());
        }
        coleccion.setActivo(false);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    @Override
    public RespuestaHttp<Void> agregarFuente(Long idColeccion, String dataSet) {
        Coleccion coleccion = coleccionesRepo.findById(idColeccion);
        FuenteEstatica fuente = new FuenteEstatica();
        fuente.setDataSet(dataSet);
        List<Hecho> hechos = fuente.leerFuente(hechosProxyRepo.findAll(),hechosDinamicaRepo.findAll(), hechosEstaticaRepo.findAll());
        coleccion.getHechos().addAll(hechos);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    @Override
    public RespuestaHttp<Void> eliminarFuente(Long idColeccion, String dataSet) {
        Coleccion coleccion = coleccionesRepo.findById(idColeccion);
        coleccion.getHechos().forEach(
                hecho -> {
                    if(hecho.getDataSets().contains(dataSet)){
                        coleccion.getHechos().remove(hecho);
                    }
                }
        );
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    @Override
    public RespuestaHttp<Void> updateColeccion(ColeccionUpdateInputDTO dto) {
        Coleccion coleccion = coleccionesRepo.findById(dto.getId_coleccion());
        if(coleccion == null){
            return new RespuestaHttp<>(null,HttpStatus.NO_CONTENT.value());
        }
        if(dto.getTitulo() != null){
            coleccion.setTitulo(dto.getTitulo());
        }
        if(dto.getDescripcion() != null){
            coleccion.setDescripcion(dto.getDescripcion());
        }
        if(dto.getHechos() != null){

        }
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    // TODO Cronjob: es importante que no se ejecute cada vez que ingresa un hecho sino en horarios de baja carga en el sistema.
    public void setearHechosConsensuados(){
        List<Coleccion> colecciones = coleccionesRepo.findAll();
        List<String> datasets = hechosEstaticaRepo.getDatasets();
        this.ejecutarAlgoritmoConsenso(colecciones, datasets);
    }

    private void ejecutarAlgoritmoConsenso(List<Coleccion> colecciones, List<String> datasets){
        colecciones.forEach(coleccion->coleccion.getAlgoritmoConsenso().ejecutarAlgoritmoConsenso(datasets));
    }
}
