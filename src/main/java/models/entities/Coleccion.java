package models.entities;

import lombok.Getter;
import lombok.Setter;
import models.entities.filtros.Filtro;
import models.entities.fuentes.Fuente;

import java.util.ArrayList;
import java.util.List;

/*
* Colecciones: conjuntos de hechos organizados bajo un título y descripción, creados y gestionados por administradores.
* Son públicas y no pueden ser editadas ni eliminadas manualmente.
* */


@Getter
public class Coleccion {
    @Setter
    private Boolean activo;

    private Long id;
    private String titulo;
    private String descripcion;
    private List<Hecho> hechos = new ArrayList<>();
    private Fuente fuente;
    private List<Filtro> criterio = new ArrayList<>();

    public void addCriterios(Filtro ... filtros){
        this.criterio.addAll(List.of(filtros));
    }

    public void addCriterios(List<Filtro> filtros){
        this.criterio.addAll(filtros);
    }

    public void addHechos(Hecho ... hechos){
        this.hechos.addAll(List.of(hechos));
    }

    public void addHechos(List<Hecho> hechos){
        this.hechos.addAll(hechos);
    }

    public Coleccion(DatosColeccion datosColeccion, long id) {
        this.titulo = datosColeccion.getTitulo();
        this.descripcion = datosColeccion.getDescripcion();
        this.fuente = datosColeccion.getFuente();
        this.id = id;
    }
}
