package models.entities;

import models.entities.filtros.Filtro;

import java.util.ArrayList;
import java.util.List;

public class Filtrador {

    public List<Hecho> aplicarFiltros(List<Filtro> filtros, List<Hecho> hechos){

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
}
