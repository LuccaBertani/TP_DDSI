package services.impl;

import models.entities.Coleccion;
import models.entities.Filtrador;
import models.entities.Hecho;
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
            List<Hecho> hechos = fuente.leerFuente();

            for (Hecho hecho : hechos){
                hechosRepo.save(hecho);
            }
        }
        else{
            throw new SecurityException("No tiene permisos para ejecutar el caso de uso");
        }

    }

    @Override
    public void navegarPorHechos(List<Filtro> filtros, Coleccion coleccion){
        Filtrador filtrador = new Filtrador();
        List<Hecho> lista = filtrador.aplicarFiltros(filtros, coleccion.getHechos());
        //Este for lo tendria que hacer otra clase (No es responsabilidad del service).
        // En el caso de que se printee de otra manera se tendria que cambiar el service y eso esta mal
        for (Hecho hecho : lista){
            System.out.println(hecho.getTitulo());
        }
    }

    @Override
    public void navegarPorHechos(Coleccion coleccion){
        //Este for lo tendria que hacer otra clase (No es responsabilidad del service).
        // En el caso de que se printee de otra manera se tendria que cambiar el service y eso esta mal
        for(Hecho hecho : coleccion.getHechos()){
            System.out.println(hecho.getTitulo());
        }
    }


}
