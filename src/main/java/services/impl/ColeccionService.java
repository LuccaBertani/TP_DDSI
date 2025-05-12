package services.impl;

import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IColeccionRepository;
import models.repositories.IHechosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.IColeccionService;

import java.util.List;

@Service
public class ColeccionService implements IColeccionService {

    private final IHechosRepository hechosRepo;
    private final IColeccionRepository coleccionesRepo;

    @Autowired
    public ColeccionService(IHechosRepository hechosRepo, IColeccionRepository coleccionesRepo) {
        this.hechosRepo = hechosRepo;
        this.coleccionesRepo = coleccionesRepo;
    }

    @Override
    public void crearColeccion(List<Filtro> criterios, DatosColeccion datos, Usuario usuario) {

        if (usuario.getRol().equals(Rol.ADMINISTRADOR)) {

            Coleccion coleccion = new Coleccion(datos,coleccionesRepo.getProxId());
            coleccion.addCriterios(criterios);
            List<Hecho> hechos = hechosRepo.findAll();
            Filtrador filtrador = new Filtrador();

            coleccion.addHechos(filtrador.aplicarFiltros(criterios, hechos));

            coleccionesRepo.save(coleccion);

        } else {
            throw new SecurityException("No tiene permisos para ejecutar el caso de uso");
        }
    }
}
