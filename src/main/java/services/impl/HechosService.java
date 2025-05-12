package services.impl;

import models.entities.Coleccion;
import models.entities.Hecho;
import models.entities.ModificadorHechos;
import models.entities.casosDeUso.NavegarPorHechos;
import models.entities.filtros.Filtro;
import models.entities.fuentes.Fuente;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IHechosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.IHechosService;
import java.util.List;

@Service
public class HechosService implements IHechosService {


    private final IHechosRepository hechosRepo;

    @Autowired
    public HechosService(IHechosRepository repo) {
        this.hechosRepo = repo;
    }

    @Override
    public void subirHecho(Hecho hecho, Usuario usuario) {
        if(usuario.getRol().equals(Rol.ADMINISTRADOR)){
            hechosRepo.save(hecho);
        }
        else{
            throw new SecurityException("No tiene permisos para ejecutar el caso de uso");
        }
    }

    @Override
    public void importarHechos(Fuente fuente, Usuario usuario){

        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){
            // Se borran y suben hechos constantemente => Guardamos los que se tienen hasta el momento en una lista
            List<Hecho> hechosActuales = hechosRepo.findAll();
            ModificadorHechos modificadorHechos = fuente.leerFuente(hechosActuales);

            List<Hecho> hechosASubir = modificadorHechos.getHechosASubir();
            List<Hecho> hechosAModificar = modificadorHechos.getHechosAModificar();

            for (Hecho hecho : hechosASubir){
                hechosRepo.save(hecho);
            }
            for (Hecho hecho : hechosAModificar){
                hechosRepo.update(hecho);
            }
        }
        else{
            throw new SecurityException("No tiene permisos para ejecutar el caso de uso");
        }
    }

    @Override
    public void navegarPorHechos(List<Filtro> filtros, Coleccion coleccion){
        var objetoNavegarPorHechos = new NavegarPorHechos();
        objetoNavegarPorHechos.navegarPorHechos(filtros,coleccion);
    }

    @Override
    public void navegarPorHechos(Coleccion coleccion){
        var objetoNavegarPorHechos = new NavegarPorHechos();
        objetoNavegarPorHechos.navegarPorHechos(coleccion);
    }


}
