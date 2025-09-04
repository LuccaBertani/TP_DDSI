package modulos.agregacion.entities;

import modulos.agregacion.entities.filtros.Filtro;
import modulos.agregacion.entities.solicitudes.Mensaje;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Filtrador {

    public static List<Hecho> aplicarFiltros(List<Filtro> filtros, List<Hecho> hechos) {
        List<Hecho> hechosFiltrados = new ArrayList<>();

        hechos.forEach(hecho -> {
            boolean pasaTodos = filtros.stream()
                    .allMatch(filtro -> filtro.aprobarHecho(hecho));
            if (pasaTodos) {
                hechosFiltrados.add(hecho);
            }
        });

        return hechosFiltrados;
    }

    public static boolean hechoPasaFiltros(List<Filtro> filtros, Hecho hecho) {
        return filtros.stream()
                .allMatch(filtro -> filtro.aprobarHecho(hecho));
    }

    public static List<Mensaje> filtrarMensajes(List<Mensaje> mensajes, Long id_usuario){
        return mensajes.stream().filter(mensaje->mensaje.getReceptor().getId().equals(id_usuario)).toList();
    }
}

