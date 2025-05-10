package services.impl;

import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.IDatosPersonalesService;

import java.util.List;

@Service
public class DatosPersonalesService implements IDatosPersonalesService {

    private final IPersonaRepository personasRepo;

    @Autowired
    public DatosPersonalesService(IPersonaRepository personasRepo) {
        this.personasRepo = personasRepo;
    }

    @Override
    public List<Usuario> obtenerListaContribuyentes(Usuario usuario){

        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return personasRepo.findAll();
        }
        else {
            throw new SecurityException("No tiene permisos para ejecutar el caso de uso");
        }
    }
}
