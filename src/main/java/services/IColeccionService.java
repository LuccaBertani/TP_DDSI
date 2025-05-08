package services;

import models.entities.Coleccion;
import models.entities.DatosColeccion;
import models.entities.Hecho;
import models.entities.filtros.Filtro;
import models.repositories.IRepository;

import java.util.List;

public interface IColeccionService {
    public void CrearColeccion(List<Filtro> criterios, DatosColeccion datos);
}
