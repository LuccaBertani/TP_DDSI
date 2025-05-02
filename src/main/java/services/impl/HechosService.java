package services.impl;

import models.entities.Hecho;
import models.entities.fuentes.Fuente;
import models.entities.personas.Persona;
import models.repositories.IHechosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import permissions.PermisoImportarHechos;
import services.IHechosService;
import java.util.List;

@Service
public class HechosService implements IHechosService {

    @Autowired
    private IHechosRepository hechosRepository;

    @Override
    public void subirHecho(Hecho hecho, Persona persona) {

        hechosRepository.save(hecho);

        if (persona.getNivel() == 0){
            persona.incrementarNivel();
        }

    }


    public void importarHechos(Fuente fuente, Persona persona){

        if (PermisoImportarHechos.tienePermisos(persona)){
            List<Hecho> hechos = fuente.leerFuente();

            for (Hecho hecho : hechos){
                hechosRepository.save(hecho);
            }
        }



    }
}
