package services.impl;

import models.entities.personas.DatosPersonalesPublicador;
import models.entities.personas.Usuario;
import models.repositories.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.IUsuarioService;

@Service
public class UsuarioService implements IUsuarioService {

    private final IPersonaRepository personasRepo;

    @Autowired
    public UsuarioService(IPersonaRepository personaRepo) {
        this.personasRepo = personaRepo;
    }
    //Momento en el que un usuario se registra y guarda datos personales (NO LLAMAR A ESTE METODO SI ES ANONIMO)
    @Override
    public void crearUsuario(String contrasenia, DatosPersonalesPublicador datosPersonales){
        Usuario usuario = new Usuario(personasRepo.getProxId());
        usuario.setDatosPersonales(datosPersonales);
        usuario.setContrasenia(contrasenia);
        personasRepo.save(usuario);
    }

}
