package models.entities.personas;

import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.fuentes.Fuente;

import java.util.ArrayList;
import java.util.List;

public class Administrador extends Visualizador {

    Usuario usuario;

    public Administrador(Usuario usuario) {
        this.usuario = usuario;
    }

    private static List<Contribuyente> listaContribuyentes = new ArrayList<>();

    // Subida manual del hecho
    public void subirHecho(Hecho hecho){
        hecho.setOrigen(Origen.CARGA_MANUAL); // Capaz en el producto final esta línea no es necesaria
        Globales.hechosTotales.add(hecho);
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
