package modulos.Front.services;

import modulos.Front.dtos.output.ColeccionOutputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service

public class ColeccionService {




    private final WebApiCallerService webApiCallerService;
    private String coleccionServiceUrl = "http://localhost:8080/api/coleccion";

    public ColeccionService(WebApiCallerService webApiCallerService){
        this.webApiCallerService = webApiCallerService;
    }


    public ResponseEntity<?> obtenerTodasLasColecciones() {
        return webApiCallerService.getList(coleccionServiceUrl + "/get-all", ColeccionOutputDTO.class);
    }



}
