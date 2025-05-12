package models.entities.casosDeUso;

import models.entities.Hecho;
import models.entities.fuentes.Fuente;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IHechosRepository;

import java.util.List;

public class ImportarHechos {

    private IHechosRepository hechosRepo;

    public ImportarHechos(IHechosRepository hechosRepo){
        this.hechosRepo = hechosRepo;
    }

    public void importarHechos(Fuente fuente, Usuario usuario){
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){
            List<Hecho> hechos = fuente.leerFuente();

            for (Hecho hecho : hechos){
                hechosRepo.save(hecho);
            }
        }
        else{
            throw new SecurityException("No tiene permisos para ejecutar el caso de uso");
        }

    }
}
