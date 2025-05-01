package models.repositories;
import models.entities.Hecho;

import java.util.List;
//Extraer hechos de la base de datos/memoria
public interface IHechosRepository {
    public List<Hecho> findAll();
    public void save(Hecho hecho);
    public void delete(Hecho hecho); // En el tp no se borran los hechos (duda)
    public Hecho findById(Long id);
}
