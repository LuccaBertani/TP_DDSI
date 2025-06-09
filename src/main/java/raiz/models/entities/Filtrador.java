package raiz.models.entities;

import raiz.models.entities.filtros.Filtro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Filtrador {

    public static List<Hecho> aplicarFiltros(Map<Class<? extends Filtro>, Filtro> filtros, List<Hecho> hechos) {
        List<Hecho> hechosFiltrados = new ArrayList<>();

        hechos.forEach(hecho -> {
            boolean pasaTodos = filtros.values().stream()
                    .allMatch(filtro -> filtro.aprobarHecho(hecho));
            if (pasaTodos) {
                hechosFiltrados.add(hecho);
            }
        });

        return hechosFiltrados;
    }

    public static boolean hechoPasaFiltros(Map<Class<? extends Filtro>, Filtro> filtros, Hecho hecho) {
        return filtros.values().stream()
                .allMatch(filtro -> filtro.aprobarHecho(hecho));
    }

    public static List<Mensaje> filtrarMensajes(List<Mensaje> mensajes, Long id_usuario){
        return mensajes.stream().filter(mensaje->mensaje.getId_receptor().equals(id_usuario)).toList();
    }
}

