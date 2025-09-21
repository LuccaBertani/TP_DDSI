package modulos.agregacion.entities.DbMain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.converters.AlgoritmoConsensoConverter;
import modulos.agregacion.entities.DbMain.algoritmosConsenso.IAlgoritmoConsenso;
import modulos.agregacion.entities.DbMain.filtros.Filtro;

import java.util.*;

/*
* Colecciones: conjuntos de hechos organizados bajo un título y descripción, creados y gestionados por administradores.
* Son públicas y no pueden ser editadas ni eliminadas manualmente.
*/


@Getter
@Setter
@Entity
@Table(name = "coleccion")
public class Coleccion {

    public Coleccion() {
    }

    public Coleccion(DatosColeccion datosColeccion) {
        this.titulo = datosColeccion.getTitulo();
        this.descripcion = datosColeccion.getDescripcion();
        hechos = new ArrayList<>();
        criterios = new ArrayList<>();
    }

    @Column (name = "activo", nullable = false)
    private Boolean activo;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "titulo", nullable = false)
    private String titulo;

    @Column (name = "descripcion")
    private String descripcion;

    //relacion muchos a muchos
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "coleccion_hecho",
            joinColumns = @JoinColumn(name = "coleccion_id"),
            inverseJoinColumns = @JoinColumn(name = "hecho_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_coleccion_hecho", columnNames = {"coleccion_id","hecho_id"})
    )
    private List<HechoRef> hechos;

    //relacion 1 a 1
    @Convert(converter = AlgoritmoConsensoConverter.class)
    @Column(name = "algoritmoConsenso", length = 50)
    private IAlgoritmoConsenso algoritmoConsenso;

    @Column(name = "modificado", nullable = false)
    private Boolean modificado;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "coleccion_filtro",
            joinColumns = @JoinColumn(name = "coleccion_id"),
            inverseJoinColumns = @JoinColumn(name = "filtro_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_coleccion_filtro", columnNames = {"coleccion_id","filtro_id"})
    )
    private List<Filtro> criterios;

    //relacion muchos a muchos
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "coleccion_hechoConsensuado",
            joinColumns = @JoinColumn(name = "coleccion_id"),
            inverseJoinColumns = @JoinColumn(name = "hechoConsensuado_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_coleccion_hechoConsensuado", columnNames = {"coleccion_id","hechoConsensuado_id"})
    )
    private List<HechoRef> hechosConsensuados = new ArrayList<>();


    // Helper para upsert por tipo (reemplaza si ya existe uno del mismo Class)
    private void upsertPorTipo(Filtro filtroNuevo) {
        criterios.removeIf(f -> f.getClass().equals(filtroNuevo.getClass()));
        criterios.add(filtroNuevo);
    }

    // Si querés un setter directo que normalice por tipo:
    public void setCriterios(List<Filtro> filtros) {
        this.criterios.clear();
        if (filtros != null) {
            for (Filtro f : filtros) upsertPorTipo(f);
        }
    }

    public void addHechos(HechoRef ... hechos){
        this.hechos.addAll(List.of(hechos));
    }

    public void addHechos(List<HechoRef> hechos){
        this.hechos.addAll(hechos);
    }




}



