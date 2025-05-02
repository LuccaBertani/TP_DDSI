package services.impl;

import models.entities.DatosPersonalesPublicador;
import models.entities.personas.Persona;
import models.repositories.IDatosPersonalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import permissions.PermisoAccesoDatosContribuyentes;
import services.IDatosPersonalesService;

import java.util.List;

@Service
public class DatosPersonalesService implements IDatosPersonalesService {

    @Autowired
    private IDatosPersonalesRepository datosPersonalesRepository;

    @Override
    public List<DatosPersonalesPublicador> obtenerListaContribuyentes(Persona persona){

        if (PermisoAccesoDatosContribuyentes.tienePermisos(persona)){
            return datosPersonalesRepository.findAll();
        }
        return null;

    }
}
