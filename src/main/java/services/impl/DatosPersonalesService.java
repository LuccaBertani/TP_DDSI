package services.impl;

import models.entities.DatosPersonalesPublicador;
import models.entities.SolicitudHecho;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IMemoriaPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.IDatosPersonalesService;

import java.util.List;

@Service
public class DatosPersonalesService implements IDatosPersonalesService {

    private final IMemoriaPersonaRepository personasRepo;

    @Autowired
    public DatosPersonalesService(IMemoriaPersonaRepository personasRepo) {
        this.personasRepo = personasRepo;
    }

    @Override
    public List<Usuario> obtenerListaContribuyentes(Usuario usuario){

        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return personasRepo.findAll();
        }
        else{
            //TODO excepcion
        }
        return null;


    }
}
