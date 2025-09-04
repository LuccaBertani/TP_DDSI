package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.converters.AlgoritmoConsensoConverter;
import modulos.agregacion.entities.algoritmosConsenso.IAlgoritmoConsenso;
import modulos.agregacion.entities.filtros.Filtro;
import modulos.agregacion.entities.filtros.IFiltro;
import modulos.shared.dtos.input.ColeccionUpdateInputDTO;

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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coleccion_hecho",
            joinColumns = @JoinColumn(name = "coleccion_id"),
            inverseJoinColumns = @JoinColumn(name = "hecho_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_coleccion_hecho", columnNames = {"coleccion_id","hecho_id"})
    )
    private List<Hecho> hechos;

    //relacion 1 a 1
    @Convert(converter = AlgoritmoConsensoConverter.class)
    @Column(name = "algoritmoConsenso", length = 50)
    private IAlgoritmoConsenso algoritmoConsenso;

    @Column(name = "modificado", nullable = false)
    private Boolean modificado;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coleccion_filtro",
            joinColumns = @JoinColumn(name = "coleccion_id"),
            inverseJoinColumns = @JoinColumn(name = "filtro_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_coleccion_filtro", columnNames = {"coleccion_id","filtro_id"})
    )
    private List<Filtro> criterios;


    public <T extends Filtro> T obtenerCriterio(Class<T> tipo) {
        return criterios.stream()
                .filter(filtro -> tipo.isInstance(filtro))
                .map(tipo::cast)
                .findFirst()
                .orElse(null); // o lanzar excepción si preferís
    }

    //relacion muchos a muchos
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coleccion_hechoConsensuado",
            joinColumns = @JoinColumn(name = "coleccion_id"),
            inverseJoinColumns = @JoinColumn(name = "hechoConsensuado_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_coleccion_hechoConsensuado", columnNames = {"coleccion_id","hechoConsensuado_id"})
    )
    private List<Hecho> hechosConsensuados = new ArrayList<>();


    // Helper para upsert por tipo (reemplaza si ya existe uno del mismo Class)
    private void upsertPorTipo(Filtro filtroNuevo) {
        criterios.removeIf(f -> f.getClass().equals(filtroNuevo.getClass()));
        criterios.add(filtroNuevo);
    }

    public void addCriterios(Filtro... filtros) {
        for (Filtro filtro : filtros) {
            upsertPorTipo(filtro); // reemplaza por tipo
        }
    }

    public void addCriterios(List<Filtro> filtros) {
        for (Filtro filtro : filtros) {
            upsertPorTipo(filtro); // reemplaza por tipo
        }
    }

    // Si querés un setter directo que normalice por tipo:
    public void setCriterios(List<Filtro> filtros) {
        this.criterios.clear();
        if (filtros != null) {
            for (Filtro f : filtros) upsertPorTipo(f);
        }
    }

    // Actualizar ahora recibe List<Filtro> en lugar de Map<...>
    public void actualizar(ColeccionUpdateInputDTO dto,
                           List<Filtro> criteriosColeccion,
                           List<Hecho> hechos) {

        if (dto.getTitulo() != null) {
            this.setTitulo(dto.getTitulo());
        }
        if (dto.getDescripcion() != null) {
            this.setDescripcion(dto.getDescripcion());
        }
        if (criteriosColeccion != null) {
            // reemplaza criterios, pero conservando la regla de “uno por tipo”
            this.setCriterios(criteriosColeccion);
        }
        if (dto.getHechos() != null) {
            if (dto.getReemplazarHechos()) {
                this.hechos = (hechos != null) ? new ArrayList<>(hechos) : new ArrayList<>();
            } else if (hechos != null) {
                this.hechos.addAll(hechos);
            }
        }
    }

    public void addHechos(Hecho ... hechos){
        this.hechos.addAll(List.of(hechos));
    }

    public void addHechos(List<Hecho> hechos){
        this.hechos.addAll(hechos);
    }


}



