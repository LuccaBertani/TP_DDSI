package services.impl;

import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.personas.Persona;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IMemoriaColeccionRepository;
import models.repositories.IMemoriaHechosRepository;
import models.repositories.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.IColeccionService;

import java.util.List;

@Service
public class ColeccionService implements IColeccionService {

    private final IMemoriaHechosRepository hechosRepo;
    private final IMemoriaColeccionRepository coleccionesRepo;

    @Autowired
    public ColeccionService(IMemoriaHechosRepository hechosRepo, IMemoriaColeccionRepository coleccionesRepo) {
        this.hechosRepo = hechosRepo;
        this.coleccionesRepo = coleccionesRepo;
    }

    @Override
    public void CrearColeccion(List<Filtro> criterios, DatosColeccion datos, Usuario usuario) {

        if (usuario.getRol().equals(Rol.ADMINISTRADOR)) {

            Coleccion coleccion = new Coleccion(datos);
            coleccion.addCriterios(criterios);
            List<Hecho> hechos = hechosRepo.findAll();

            Filtrador filtrador = new Filtrador();

            coleccion.addHechos(filtrador.aplicarFiltros(criterios, hechos));

            coleccionesRepo.save(coleccion);

        } else {
            //tirar excepcion
        }
    }
}
