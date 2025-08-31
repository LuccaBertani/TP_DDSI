package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.converters.AlgoritmoConsensoConverter;
import modulos.shared.Hecho;
import modulos.agregacion.entities.algoritmosConsenso.IAlgoritmoConsenso;
import modulos.agregacion.entities.filtros.Filtro;
import modulos.shared.RespuestaHttp;
import modulos.shared.dtos.input.ColeccionUpdateInputDTO;
import modulos.shared.dtos.input.CriteriosColeccionDTO;
import org.springframework.http.HttpStatus;

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
        criterios = new HashMap<>();
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


    private Map<Class<? extends Filtro>, Filtro> criterios;


    public <T extends Filtro> T obtenerCriterio(Class<T> tipo) {
        return tipo.cast(this.criterios.get(tipo));
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

    public void actualizar(ColeccionUpdateInputDTO dto, Map<Class<? extends Filtro>, Filtro> criteriosColeccion, List<Hecho> hechos){

        if(dto.getTitulo() != null){
            this.setTitulo(dto.getTitulo());
        }
        if(dto.getDescripcion() != null){
            this.setDescripcion(dto.getDescripcion());
        }
        if(criteriosColeccion != null){
            this.criterios = criteriosColeccion;
        }
        if(dto.getHechos() != null){
            if(dto.getReemplazarHechos()){
                this.hechos = hechos;
            }
            else{
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



