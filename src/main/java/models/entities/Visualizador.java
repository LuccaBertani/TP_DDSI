package models.entities;

import lombok.Getter;
import lombok.Setter;
import models.entities.filtros.Filtro;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class Visualizador {
    private DatosPersonalesPublicador datosPersonales; // Si se setearon, significa que el publicador inició sesión

    public void solicitudSubirHecho(Hecho hecho, ContextoPersona GestorPersona){
        Globales.solicitudesSubirHecho.add(new SolicitudHecho(GestorPersona, hecho));
    }

    public void navegarPorHechos(List<Filtro> filtros, Coleccion coleccion){
        Set<Hecho> lista = aplicarFiltros(filtros, coleccion);
        for (Hecho hecho : lista){
            System.out.println(hecho.getTitulo());
        }
    }
    public void navegarPorHechos(Coleccion coleccion){
        for(Hecho hecho : coleccion.getHechos()){
            System.out.println(hecho.getTitulo());
        }
    }

    // Devuelve los hechos que cumplan con todos los filtros
    private Set<Hecho> aplicarFiltros(List<Filtro> filtros, Coleccion coleccion){
        return coleccion.getHechos().stream().filter(
                hecho -> filtros.
                        stream().
                        allMatch(filtro->filtro.aprobarHecho(hecho))
        ).collect(Collectors.toSet());
    }
}
