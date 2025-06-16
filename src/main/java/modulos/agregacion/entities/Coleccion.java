package modulos.agregacion.entities;

import lombok.Getter;
import lombok.Setter;
import modulos.shared.Hecho;
import modulos.agregacion.entities.algoritmosConsenso.IAlgoritmoConsenso;
import modulos.agregacion.entities.filtros.Filtro;

import java.util.*;

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
    private IAlgoritmoConsenso algoritmoConsenso;

    private Map<Class<? extends Filtro>, Filtro> criterios = new HashMap<>();

    public <T extends Filtro> T obtenerCriterio(Class<T> tipo) {
        return tipo.cast(this.criterios.get(tipo));
    }

    // Con un set debido a que no usamos snapshot acá, evitamos repetidos
    private Set<Hecho> hechosConsensuados = new HashSet<>();

    public Coleccion(DatosColeccion datosColeccion, long id) {
        this.titulo = datosColeccion.getTitulo();
        this.descripcion = datosColeccion.getDescripcion();
        this.id = id;
    }

    public void addCriterios(Filtro... filtros) {
        for (Filtro filtro : filtros) {
            this.criterios.put(filtro.getClass(), filtro);  // Sobrescribe si ya había uno del mismo tipo
        }
    }

    public void addCriterios(List<Filtro> filtros){
        for (Filtro filtro : filtros) {
            this.criterios.put(filtro.getClass(), filtro);  // Sobrescribe si ya había uno del mismo tipo
        }
    }

    public void addHechos(Hecho ... hechos){
        this.hechos.addAll(List.of(hechos));
    }

    public void addHechos(List<Hecho> hechos){
        this.hechos.addAll(hechos);
    }
}
