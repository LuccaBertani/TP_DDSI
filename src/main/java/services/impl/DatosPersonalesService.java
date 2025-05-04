package services.impl;

import models.entities.DatosPersonalesPublicador;
import models.entities.SolicitudHecho;
import models.entities.personas.Persona;
import models.repositories.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import permissions.PermisoAccesoDatosContribuyentes;
import services.IDatosPersonalesService;

import java.util.List;

@Service
public class DatosPersonalesService implements IDatosPersonalesService {

    private final IRepository<Persona> personasRepo;

    @Autowired
    public DatosPersonalesService(IRepository<Persona> personasRepo) {
        this.personasRepo = personasRepo;
    }

    @Override
    public List<Persona> obtenerListaContribuyentes(Persona persona){

        if (PermisoAccesoDatosContribuyentes.tienePermisos(persona)){
            return personasRepo.findAll();
        }
        return null;

    }
}
