package modulos.agregacion.services;


import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.DatosColeccion;
import modulos.agregacion.entities.Filtrador;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaAbsoluta;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaSimple;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMultiplesMenciones;
import modulos.agregacion.entities.filtros.FiltroCategoria;
import modulos.agregacion.entities.filtros.FiltroFechaAcontecimiento;
import modulos.agregacion.entities.filtros.FiltroFechaCarga;
import modulos.agregacion.entities.filtros.FiltroPais;
import modulos.agregacion.repositories.*;
import modulos.fuentes.Dataset;
import modulos.fuentes.FuenteEstatica;
import modulos.shared.Hecho;
import modulos.shared.RespuestaHttp;
import modulos.shared.dtos.input.ColeccionInputDTO;
import modulos.shared.dtos.input.ColeccionUpdateInputDTO;
import modulos.shared.dtos.input.CriteriosColeccionDTO;
import modulos.shared.dtos.input.ModificarConsensoInputDTO;
import modulos.usuario.Rol;
import modulos.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import modulos.agregacion.entities.FormateadorHecho;
import modulos.agregacion.entities.FiltrosColeccion;
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
            if (dtoInput.getAlgoritmoConsenso().equals("mayoria-absoluta"))
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaAbsoluta(coleccion));
            else if (dtoInput.getAlgoritmoConsenso().equals("mayoria-simple"))
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMayoriaSimple(coleccion));
            else if (dtoInput.getAlgoritmoConsenso().equals("multiples-menciones"))
                coleccion.setAlgoritmoConsenso(new AlgoritmoConsensoMultiplesMenciones(coleccion));
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
//TODO El orden fijo global para guardar criterios en la lista de criterios de la coleccion es [filtroCategoria,filtroFechaCarga,filtroFechaAcontecimiento,filtroPais,filtroContenidoMultimedia,FiltroDescripcion,FiltroOrigen,FiltroTitulo]
    public RespuestaHttp<List<ColeccionOutputDTO>> obtenerTodasLasColecciones(){

        List<ColeccionOutputDTO> listaDTO = new ArrayList<>();

        List<Coleccion> colecciones = coleccionesRepo.findAll();

        for (Coleccion coleccion : colecciones){
            ColeccionOutputDTO dto = new ColeccionOutputDTO();
            dto.setId(coleccion.getId());
            dto.setNombre(coleccion.getTitulo());
            dto.setDescripcion(coleccion.getDescripcion());

            CriteriosColeccionDTO criterios = new CriteriosColeccionDTO();


            FiltroCategoria categoria = coleccion.obtenerCriterio(FiltroCategoria.class);

            if(categoria != null) {
                criterios.setCategoria(categoria.getCategoria().getTitulo());
            }else{
                criterios.setCategoria("N/A");
            }
            FiltroFechaCarga filtroFechaCarga = coleccion.obtenerCriterio(FiltroFechaCarga.class);

            if(filtroFechaCarga != null) {
                criterios.setFechaCargaInicial(filtroFechaCarga.getFechaInicial().toString());
                criterios.setFechaCargaFinal(filtroFechaCarga.getFechaFinal().toString());
            }else{
                criterios.setFechaCargaInicial("N/A");
                criterios.setFechaCargaFinal("N/A");
            }

            FiltroFechaAcontecimiento filtroFechaAcontecimiento = coleccion.obtenerCriterio(FiltroFechaAcontecimiento.class);

            if(filtroFechaAcontecimiento != null) {
                criterios.setFechaAcontecimientoInicial(filtroFechaAcontecimiento.getFechaInicial().toString());
                criterios.setFechaAcontecimientoFinal(filtroFechaAcontecimiento.getFechaFinal().toString());
            }
            else{
                criterios.setFechaAcontecimientoInicial("N/A");
                criterios.setFechaAcontecimientoFinal("N/A");
            }

            FiltroPais filtroPais = coleccion.obtenerCriterio(FiltroPais.class);

            if(filtroPais != null) {
                criterios.setPais(filtroPais.getPais().toString());
            }
            else{
                criterios.setPais("N/A");
            }

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
        dto.setNombre(coleccion.getTitulo());
        dto.setDescripcion(coleccion.getDescripcion());

        CriteriosColeccionDTO criterios = new CriteriosColeccionDTO();


        FiltroCategoria categoria = coleccion.obtenerCriterio(FiltroCategoria.class);

        if(categoria != null) {
            criterios.setCategoria(categoria.getCategoria().getTitulo());
        }else{
            criterios.setCategoria("N/A");
        }
        FiltroFechaCarga filtroFechaCarga = coleccion.obtenerCriterio(FiltroFechaCarga.class);

        if(filtroFechaCarga != null) {
            criterios.setFechaCargaInicial(filtroFechaCarga.getFechaInicial().toString());
            criterios.setFechaCargaFinal(filtroFechaCarga.getFechaFinal().toString());
        }else{
            criterios.setFechaCargaInicial("N/A");
            criterios.setFechaCargaFinal("N/A");
        }

        FiltroFechaAcontecimiento filtroFechaAcontecimiento = coleccion.obtenerCriterio(FiltroFechaAcontecimiento.class);

        if(filtroFechaAcontecimiento != null) {
            criterios.setFechaAcontecimientoInicial(filtroFechaAcontecimiento.getFechaInicial().toString());
            criterios.setFechaAcontecimientoFinal(filtroFechaAcontecimiento.getFechaFinal().toString());
        }
        else{
            criterios.setFechaAcontecimientoInicial("N/A");
            criterios.setFechaAcontecimientoFinal("N/A");
        }

        FiltroPais filtroPais = coleccion.obtenerCriterio(FiltroPais.class);

        if(filtroPais != null) {
            criterios.setPais(filtroPais.getPais().toString());
        }
        else{
            criterios.setPais("N/A");
        }

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


    // TODO: adaptarlo a ids
    public RespuestaHttp<Void> agregarFuente(Long idColeccion, String dataSet) {
        Coleccion coleccion = coleccionesRepo.findById(idColeccion);
        FuenteEstatica fuente = new FuenteEstatica();
        fuente.setDataSet(dataSet);
        //List<Hecho> hechos = fuente.leerFuente(hechosProxyRepo.findAll(),hechosDinamicaRepo.findAll(), hechosEstaticaRepo.findAll());



        //coleccion.getHechos().addAll(hechos);
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

    // TODO: Adaptarlo a ids
    public RespuestaHttp<Void> eliminarFuente(Long idColeccion, String dataSet) {
        Coleccion coleccion = coleccionesRepo.findById(idColeccion);
        coleccion.getHechos().forEach(
                hecho -> {
                    if(hecho.getDatasets().contains(dataSet)){
                        coleccion.getHechos().remove(hecho);
                    }
                }
        );
        return new RespuestaHttp<>(null,HttpStatus.OK.value());
    }

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

    @Scheduled(cron = "0 0 3 * * *")
    public void setearHechosConsensuados(){
        List<Coleccion> colecciones = coleccionesRepo.findAll();
        List<Dataset> datasets = hechosEstaticaRepo.getDatasets();
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
