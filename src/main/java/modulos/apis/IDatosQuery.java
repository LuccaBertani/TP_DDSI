package modulos.apis;

import modulos.agregacion.entities.DbMain.*;
import modulos.servicioEstadistica.entities.CategoriaCantidad;
import modulos.servicioEstadistica.entities.CategoriaHora;
import modulos.servicioEstadistica.entities.CategoriaProvincia;
import modulos.servicioEstadistica.entities.ColeccionProvincia;

import java.util.List;

public interface IDatosQuery {
    //De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados?
    List<ColeccionProvincia> obtenerMayorCantHechosProvinciaEnColeccion();
    //¿Cuál es la categoría con mayor cantidad de hechos reportados?
    CategoriaCantidad categoriaMayorCantHechos();
    // ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
    List<CategoriaProvincia> mayorCantHechosCategoriaXProvincia();
    //¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
    List<CategoriaHora> horaMayorCantHechos();
    //¿Cuántas solicitudes de eliminación son spam?
    CantSolicitudesEliminacionSpam cantSolicitudesEliminacionSpam();
}

