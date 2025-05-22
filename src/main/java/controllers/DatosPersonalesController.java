package controllers;

import models.dtos.input.DatosPersonalesInputDTO;
import models.dtos.output.DatosPersonalesOutputDTO;
import models.entities.RespuestaHttp;
import models.entities.personas.DatosPersonalesPublicador;
import models.entities.personas.Usuario;
import models.repositories.IColeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.IColeccionService;
import services.IDatosPersonalesService;
import java.util.List;

@RestController
@RequestMapping("/api/datos-personales")
@CrossOrigin(origins = "http://localhost:3000")
public class DatosPersonalesController {

    private IDatosPersonalesService datosPersonalesService;

    public DatosPersonalesController(IDatosPersonalesService datosPersonalesService){
        this.datosPersonalesService = datosPersonalesService;
    }



    @GetMapping("/contribuyentes")
    public DatosPersonalesOutputDTO buscarTodos(DatosPersonalesInputDTO inputDto) {

        RespuestaHttp<List<Usuario>> respuestaHttp = datosPersonalesService.obtenerListaContribuyentes(inputDto);
        DatosPersonalesOutputDTO outputDTO = new DatosPersonalesOutputDTO();


    }


}
