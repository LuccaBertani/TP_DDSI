package services.impl;

import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.personas.Persona;
import models.repositories.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.IColeccionService;

import java.util.List;

@Service
public class ColeccionService implements IColeccionService {

    private final IRepository<Hecho> hechosRepo;
    private final IRepository<Coleccion> coleccionesRepo;

    @Autowired
    public ColeccionService(IRepository<Hecho> hechosRepo, IRepository<Coleccion> coleccionesRepo) {
        this.hechosRepo = hechosRepo;
        this.coleccionesRepo = coleccionesRepo;
    }

    @Override
    public void CrearColeccion(List<Filtro> criterios, DatosColeccion datos){

        Coleccion coleccion = new Coleccion(datos);
        coleccion.addCriterios(criterios);
        List<Hecho> hechos = hechosRepo.findAll();

        Filtrador filtrador = new Filtrador();

        coleccion.addHechos(filtrador.aplicarFiltros(criterios,hechos));

        coleccionesRepo.save(coleccion);

    }

}
