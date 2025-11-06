package modulos.Front.services;

import modulos.Front.dtos.output.*;
import modulos.Front.dtos.input.GetHechosColeccionInputDTO;
import modulos.Front.dtos.input.ImportacionHechosInputDTO;
import modulos.Front.dtos.input.SolicitudHechoInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class HechosService {
    private final WebApiCallerService webApiCallerService;
    private String hechoServiceUrl = "/api/hechos";

    public HechosService(WebApiCallerService webApiCallerService){
        this.webApiCallerService = webApiCallerService;
    }


    public ResponseEntity<?> getHecho(Long id_hecho, String fuente) {
        return webApiCallerService.getEntitySinToken(this.hechoServiceUrl + "/public/get?id_hecho=" + id_hecho + "&fuente=" + fuente, VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> subirHecho(SolicitudHechoInputDTO hechoInputDTO) {
        return webApiCallerService.postEntity(this.hechoServiceUrl + "/subir", hechoInputDTO, Void.class);
    }

    public ResponseEntity<?> importarHechos(ImportacionHechosInputDTO dtoInput, MultipartFile file) {
        return webApiCallerService.importarHecho(file, dtoInput);
    }

    public ResponseEntity<?> getHechos() {
        return webApiCallerService.getListSinToken(this.hechoServiceUrl + "/public/get-all?origen=0", VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> getHechosFiltradosColeccion(GetHechosColeccionInputDTO inputDTO) {
        return webApiCallerService.postList(this.hechoServiceUrl + "/public/get/filtrar", inputDTO, VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> getHechosConLatitudYLongitud(){
        return webApiCallerService.getListSinToken(this.hechoServiceUrl + "/public/get-mapa?origen=0", VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> getPaises() {
        return webApiCallerService.getListSinToken(this.hechoServiceUrl + "/public/paises/get-all", PaisDto.class);
    }

    public ResponseEntity<?> getProvinciasByIdPais(Long id_pais) {
        return webApiCallerService.getListSinToken(this.hechoServiceUrl + "/public/provincias?id_pais="+id_pais, ProvinciaDto.class);
    }

    public ResponseEntity<?> getCategorias() {
        return webApiCallerService.getListSinToken(this.hechoServiceUrl + "/public/categorias/get-all", CategoriaDto.class);
    }

    public ResponseEntity<Long> getCantHechos() {
        return webApiCallerService.getEntitySinToken(this.hechoServiceUrl + "/public/cantHechos", Long.class);
    }

    public ResponseEntity<?> getPaisYProvincia(Double latitud, Double longitud){
        return webApiCallerService.getEntitySinToken(this.hechoServiceUrl + "/public/pais-provincia?latitud=" + latitud + "&longitud=" + longitud
                , PaisProvinciaDTO.class);
    }
}
