package controllers;

import models.dtos.output.DatosPersonalesOutputDTO;
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
@RequestMapping("/api/datosPersonales")
@CrossOrigin(origins = "http://localhost:3000")
public class DatosPersonalesController {

    @Autowired
    private IDatosPersonalesService datosPersonalesService;

    @GetMapping
    public List<DatosPersonalesOutputDTO> buscarTodos() {
        return this.datosPersonalesService();
    }


}
