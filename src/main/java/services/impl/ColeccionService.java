package services.impl;

import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.personas.Persona;
import models.repositories.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColeccionService {

    private final IRepository<Hecho> hechosRepo;
    private final IRepository<Coleccion> coleccionesRepo;

    @Autowired
    public ColeccionService(IRepository<Hecho> hechosRepo, IRepository<Coleccion> coleccionesRepo) {
        this.hechosRepo = hechosRepo;
        this.coleccionesRepo = coleccionesRepo;
    }

    public void CrearColeccion(List<Filtro> criterios, DatosColeccion datos){

        Coleccion coleccion = new Coleccion(datos);
        coleccion.addCriterios(criterios);
        List<Hecho> hechos = hechosRepo.findAll();

        Filtrador filtrador = new Filtrador();

        coleccion.addHechos(filtrador.aplicarFiltros(criterios,hechos));

        coleccionesRepo.save(coleccion);


    }

}
