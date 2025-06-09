package modulos.agregacion.entities;

import modulos.shared.Hecho;
import modulos.agregacion.entities.filtros.Filtro;
import modulos.solicitudes.Mensaje;

import java.util.ArrayList;
import java.util.List;

public class Filtrador {

    public static List<Hecho> aplicarFiltros(List<Filtro> filtros, List<Hecho> hechos){

        List<Hecho> hechosFiltrados = new ArrayList<>();

        hechos.forEach(hecho -> {
            Boolean condicion = filtros.stream()
                    .allMatch(criterio -> criterio.aprobarHecho(hecho));
            if(condicion) {
                hechosFiltrados.add(hecho);
            }
        });

        return hechosFiltrados;
    }

    public static Boolean hechoPasaFiltros(List<Filtro> filtros, Hecho hecho){
        List<Hecho> hechosFiltrados = new ArrayList<>();
        return filtros.stream()
                            .allMatch(criterio -> criterio.aprobarHecho(hecho));
    }

    public static List<Mensaje> filtrarMensajes(List<Mensaje> mensajes, Long id_usuario){
        return mensajes.stream().filter(mensaje->mensaje.getId_receptor().equals(id_usuario)).toList();
    }
}

