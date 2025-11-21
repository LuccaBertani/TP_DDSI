package modulos.Front.services;

import modulos.Front.dtos.input.HechoModificarInputDTO;
import modulos.Front.dtos.output.*;
import modulos.Front.dtos.input.GetHechosColeccionInputDTO;
import modulos.Front.dtos.input.ImportacionHechosInputDTO;
import modulos.Front.dtos.input.SolicitudHechoInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class HechosService {
    private final WebApiCallerService webApiCallerService;
    private String hechoServiceUrl = "/api/hechos";

    public HechosService(WebApiCallerService webApiCallerService){
        this.webApiCallerService = webApiCallerService;
    }


    public ResponseEntity<?> getHecho(Long id_hecho, String fuente) {
        return webApiCallerService.getEntityTokenOpcional(this.hechoServiceUrl + "/public/get?id_hecho=" + id_hecho + "&fuente=" + fuente, VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> subirHecho(SolicitudHechoInputDTO hechoInputDTO) {
        return webApiCallerService.postMultipartHecho(this.hechoServiceUrl + "/subir", hechoInputDTO, Void.class);
    }

    public ResponseEntity<?> importarHechos(ImportacionHechosInputDTO dtoInput, MultipartFile file) {
        return webApiCallerService.importarHecho(file, dtoInput);
    }

    public ResponseEntity<?> getHechos() {
        return webApiCallerService.getListTokenOpcional(this.hechoServiceUrl + "/public/get-all?origen=0", VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> getHechosFiltradosColeccion(GetHechosColeccionInputDTO inputDTO) {
        return webApiCallerService.postListTokenOpcional(this.hechoServiceUrl + "/public/get/filtrar", inputDTO, VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> getHechosConLatitudYLongitud(){
        return webApiCallerService.getListTokenOpcional(this.hechoServiceUrl + "/public/get-mapa?origen=0", VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> getPaises() {
        return webApiCallerService.getListTokenOpcional(this.hechoServiceUrl + "/public/paises/get-all", PaisDto.class);
    }

    public ResponseEntity<?> getProvinciasByIdPais(Long id_pais) {
        return webApiCallerService.getListTokenOpcional(this.hechoServiceUrl + "/public/provincias?id_pais="+id_pais, ProvinciaDto.class);
    }

    public ResponseEntity<?> getCategorias() {
        return webApiCallerService.getListTokenOpcional(this.hechoServiceUrl + "/public/categorias/get-all", CategoriaDto.class);
    }

    public ResponseEntity<Long> getCantHechos() {
        return webApiCallerService.getEntityTokenOpcional(this.hechoServiceUrl + "/public/cantHechos", Long.class);
    }

    public ResponseEntity<?> getPaisYProvincia(Double latitud, Double longitud){
        return webApiCallerService.getEntityTokenOpcional(this.hechoServiceUrl + "/public/pais-provincia?latitud=" + latitud + "&longitud=" + longitud
                , PaisProvinciaDTO.class);
    }

    public ResponseEntity<?> getHechosDelUsuario(){
        // Llama al endpoint del backend: /api/hechos/mis-hechos
        return webApiCallerService.getList(
                this.hechoServiceUrl + "/mis-hechos",
                VisualizarHechosOutputDTO.class
        );
    }

    public ResponseEntity<List<VisualizarHechosOutputDTO>> getHechosDestacados() {
        return webApiCallerService.getListTokenOpcional(this.hechoServiceUrl + "/public/destacados", VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> eliminarHecho(Long id, String fuente){
        return webApiCallerService.postEntity(this.hechoServiceUrl + "/eliminar-hecho?id=" + id + "&fuente=" + fuente, Void.class);
    }

    public ResponseEntity<?> modificarHecho(HechoModificarInputDTO dto){
        return webApiCallerService.postEntity(this.hechoServiceUrl + "/modificar-hecho", dto, Void.class);
    }

    public ResponseEntity<Integer> getCantFuentes() {
        return webApiCallerService.getEntity(this.hechoServiceUrl + "/cantFuentes", Integer.class);
    }
}
