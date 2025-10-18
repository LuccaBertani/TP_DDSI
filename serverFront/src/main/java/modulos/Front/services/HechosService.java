package modulos.Front.services;

import jakarta.validation.Valid;
import modulos.Front.dtos.input.GetHechosColeccionInputDTO;
import modulos.Front.dtos.input.ImportacionHechosInputDTO;
import modulos.Front.dtos.input.SolicitudHechoInputDTO;
import modulos.Front.dtos.output.HechosResponse;
import modulos.Front.dtos.output.VisualizarHechosOutputDTO;
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
        return webApiCallerService.getEntity(this.hechoServiceUrl + "/get?id_hecho=" + id_hecho.toString() + "&fuente=" + fuente, Void.class);
    }

    public ResponseEntity<?> subirHecho(SolicitudHechoInputDTO hechoInputDTO) {
        return webApiCallerService.postEntity(this.hechoServiceUrl + "/subir", hechoInputDTO, Void.class);
    }

    public ResponseEntity<?> importarHechos(ImportacionHechosInputDTO dtoInput, MultipartFile file) {
        return webApiCallerService.importarHecho(file, dtoInput);
    }

    public ResponseEntity<?> getHechos() {
        return webApiCallerService.getList(this.hechoServiceUrl + "/public/get-all?origen=0", VisualizarHechosOutputDTO.class);
    }

    public ResponseEntity<?> getHechosFiltradosColeccion(GetHechosColeccionInputDTO inputDTO) {
        return webApiCallerService.postList(this.hechoServiceUrl + "/public/get/filtrar", inputDTO, GetHechosColeccionInputDTO.class);
    }
}
