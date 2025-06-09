package modulos.agregacion.entities;

import lombok.Getter;
import lombok.Setter;
import modulos.shared.Hecho;
import modulos.agregacion.entities.algoritmosConsenso.IAlgoritmoConsenso;
import modulos.agregacion.entities.filtros.Filtro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
* Colecciones: conjuntos de hechos organizados bajo un título y descripción, creados y gestionados por administradores.
* Son públicas y no pueden ser editadas ni eliminadas manualmente.
* */


@Getter
@Setter
public class Coleccion {

    private Boolean activo;

    private Long id;
    private String titulo;
    private String descripcion;
    private List<Hecho> hechos = new ArrayList<>();
    private List<Filtro> criterio = new ArrayList<>();
    private IAlgoritmoConsenso algoritmoConsenso;

    // Con un set debido a que no usamos snapshot acá, evitamos repetidos
    private Set<Hecho> hechosConsensuados = new HashSet<>();

    public Coleccion(DatosColeccion datosColeccion, long id) {
        this.titulo = datosColeccion.getTitulo();
        this.descripcion = datosColeccion.getDescripcion();
        this.id = id;
    }

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
}
