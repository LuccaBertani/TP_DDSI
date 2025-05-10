package services.impl;

import models.entities.DatosPersonalesPublicador;
import models.entities.personas.Usuario;
import models.repositories.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final IPersonaRepository personaRepo;

    @Autowired
    public UsuarioService(IPersonaRepository personaRepo) {
        this.personaRepo = personaRepo;
    }
    //Momento en el que un usuario se registra y guarda datos personales (NO LLAMAR A ESTE METODO SI ES ANONIMO)
    public void crearUsuario(String contrasenia, DatosPersonalesPublicador datosPersonales){
        Usuario usuario = new Usuario(personaRepo.getProxId());
        usuario.setDatosPersonales(datosPersonales);
        usuario.setContrasenia(contrasenia);
        personaRepo.save(usuario);
    }

}
