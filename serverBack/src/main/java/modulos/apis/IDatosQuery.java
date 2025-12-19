package modulos.apis;

import modulos.agregacion.entities.DbMain.*;
import modulos.servicioEstadistica.entities.CategoriaCantidad;
import modulos.servicioEstadistica.entities.CategoriaHora;
import modulos.servicioEstadistica.entities.CategoriaProvincia;
import modulos.servicioEstadistica.entities.ColeccionProvincia;

import java.util.List;

public interface IDatosQuery {
    List<ColeccionProvincia> obtenerMayorCantHechosProvinciaEnColeccion();
    CategoriaCantidad categoriaMayorCantHechos();
    List<CategoriaProvincia> mayorCantHechosCategoriaXProvincia();
    List<CategoriaHora> horaMayorCantHechos();
    Long cantSolicitudesEliminacionSpam();
    Categoria findCategoriaById(Long categoria_id);
    Provincia findProvinciaById(Long id);
    Coleccion findColeccionById(Long id);
}

