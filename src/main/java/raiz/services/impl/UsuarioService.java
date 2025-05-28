package raiz.services.impl;

import raiz.models.dtos.input.UsuarioInputDTO;
import raiz.models.entities.RespuestaHttp;
import raiz.models.entities.personas.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import raiz.models.repositories.IPersonaRepository;
import raiz.services.IUsuarioService;

@Service
public class UsuarioService implements IUsuarioService {

    private final IPersonaRepository personasRepo;

    @Autowired
    public UsuarioService(IPersonaRepository personaRepo) {
        this.personasRepo = personaRepo;
    }
    //Momento en el que un usuario se registra y guarda datos personales (NO LLAMAR A ESTE METODO SI ES ANONIMO)
    @Override
    public RespuestaHttp<Usuario> crearUsuario(UsuarioInputDTO inputDTO){

        Usuario usuario = new Usuario(personasRepo.getProxId());

        usuario.getDatosPersonales().setNombre(inputDTO.getNombre());
        usuario.getDatosPersonales().setApellido(inputDTO.getApellido());
        usuario.getDatosPersonales().setEdad(inputDTO.getEdad());
        usuario.setContrasenia(inputDTO.getContrasenia());
        personasRepo.save(usuario);
        return new RespuestaHttp<>(usuario, HttpStatus.OK.value());
    }

}
