package services;

import models.entities.Coleccion;
import models.entities.DatosColeccion;
import models.entities.Hecho;
import models.entities.filtros.Filtro;
import models.entities.personas.Usuario;

import java.util.List;

public interface IColeccionService {
    public void crearColeccion(List<Filtro> criterios, DatosColeccion datos, Usuario usuario);
}
