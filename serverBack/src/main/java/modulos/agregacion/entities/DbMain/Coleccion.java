package modulos.agregacion.entities.DbMain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.converters.AlgoritmoConsensoConverter;
import modulos.agregacion.entities.DbMain.algoritmosConsenso.IAlgoritmoConsenso;
import modulos.agregacion.entities.DbMain.filtros.Filtro;
import modulos.agregacion.entities.DbMain.hechoRef.HechoRef;

import java.util.*;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "coleccion_hecho",
            joinColumns = @JoinColumn(name = "coleccion_id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "hecho_id",     referencedColumnName = "id"),
                    @JoinColumn(name = "hecho_fuente", referencedColumnName = "fuente")
            },
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_coleccion_hecho",
                    columnNames = {"coleccion_id", "hecho_id", "hecho_fuente"}
            )
    )
    private List<HechoRef> hechos;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "coleccion_hecho_consensuado",
            joinColumns = @JoinColumn(name = "coleccion_id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "hecho_id",     referencedColumnName = "id"),
                    @JoinColumn(name = "hecho_fuente", referencedColumnName = "fuente")
            },
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_coleccion_hecho_consensuado",
                    columnNames = {"coleccion_id", "hecho_id", "hecho_fuente"}
            )
    )
    private List<HechoRef> hechosConsensuados = new ArrayList<>();

    @Column(name = "cant_accesos")
    private Long cant_accesos;

    public void setCriterios(List<Filtro> filtros) {
        this.criterios.clear();
        if (filtros != null) {
            this.criterios.addAll(filtros);
        }
    }

    public void incrementarAccesos(){
        this.cant_accesos++;
    }

    @PrePersist
    protected void onCreate() {
        if (cant_accesos == null) {
            cant_accesos = 0L;
        }
    }

    public void addHechos(HechoRef ... hechos){
        this.hechos.addAll(List.of(hechos));
    }

    public void addHechos(List<HechoRef> hechos){
        this.hechos.addAll(hechos);
    }



}



