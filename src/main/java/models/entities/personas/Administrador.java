package models.entities.personas;

import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.fuentes.Fuente;

import java.util.ArrayList;
import java.util.List;

public class Administrador extends Visualizador {

    private static List<Contribuyente> listaContribuyentes = new ArrayList<>();

    // Subida manual del hecho
    public void subirHecho(Hecho hecho){
        hecho.setOrigen(Origen.CARGA_MANUAL); // Capaz en el producto final esta línea no es necesaria
        Globales.hechosTotales.add(hecho);
    }


    //Criterios es una lista traida supongo del front donde el usuario indica los filtros y la lista de hechos totales es una global
    public void crearColeccion(DatosColeccion datosColeccion, List<Filtro> criterios, List<Hecho> hechosTotales){
        Coleccion coleccion = new Coleccion(datosColeccion);
        coleccion.getCriterio().addAll(criterios);
        Globales.hechosTotales.forEach(hecho -> {
            Boolean condicion = coleccion.getCriterio().stream()
                    .allMatch(criterio -> criterio.aprobarHecho(hecho));
            if(condicion) {
            coleccion.getHechos().add(hecho);
            }
            Globales.coleccionesTotales.add(coleccion);
        });

    }
    //Esos hechos habrian que agregarlos a la lista global
    public void importarHechos(Fuente fuente){
        Globales.hechosTotales.addAll(fuente.leerFuente());
    }

    //Me imagino que este metodo se llama en el caso que el administrador acepte la solicitud de eliminacion
    public void evaluarSolicitudEliminacion(SolicitudHecho solicitud, Boolean respuesta){
        if(respuesta){
            Globales.hechosTotales.remove(solicitud.getHecho());
            Contribuyente contribuyente = (Contribuyente) solicitud.getGestorPersona().getVisualizador();
            contribuyente.disminuirHechosSubidos();
            if(contribuyente.getCantHechosSubidos() == 0){
                solicitud.getGestorPersona().ContribuyenteAVisualizador();
            }
        }
        Globales.solicitudesEliminacion.remove(solicitud);
    }
    //la respuesta viene del boton de front-end
    public void evaluarSolicitudSubirHecho(SolicitudHecho solicitud, Boolean respuesta){
        if(respuesta){
            Globales.hechosTotales.add(solicitud.getHecho());
            // TODO
            if(solicitud.getGestorPersona().getVisualizador().getClass().getSimpleName().equals("Visualizador")
                && !solicitud.getGestorPersona().getVisualizador().getDatosPersonales().getNombre().isEmpty()){
                solicitud.getGestorPersona().VisualizadorAContribuyente();//cambio de estado
                listaContribuyentes.add((Contribuyente) solicitud.getGestorPersona().getVisualizador());
            }
            else{
                Contribuyente contribuyente = (Contribuyente) solicitud.getGestorPersona().getVisualizador();
                contribuyente.incrementarHechosSubidos();

            }
        }
        Globales.solicitudesSubirHecho.remove(solicitud);
    }

}
