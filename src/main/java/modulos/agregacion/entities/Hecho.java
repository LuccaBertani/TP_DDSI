package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.fuentes.Dataset;
import modulos.agregacion.entities.usuario.Usuario;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "hecho")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "fuente", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Hecho {

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_hecho"))
    private Usuario usuario;

    @Embedded
    private AtributosHecho atributosHecho;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hecho_dataset",
            joinColumns = @JoinColumn(name = "hecho_id"),
            inverseJoinColumns = @JoinColumn(name = "dataset_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_hecho_dataset", columnNames = {"hecho_id","dataset_id"})
    )
    private List<Dataset> datasets;

    public Hecho() {
        this.datasets = new ArrayList<>();
        //this.atributosHecho = atributosHecho;
    }

}

/*


Many To One



metodo falopa(){

hecho1

hecho1.setNombre("lucca);

save()
)
}

* PERSIST → si persisto el padre, persiste también el hijo.

MERGE → si actualizo el padre, mergea también el hijo.

REMOVE → si borro el padre, borra también el hijo.

REFRESH → si refresco el padre, refresca también el hijo.

DETACH → si saco al padre del contexto (detach), también el hijo.

ALL → equivale a todas las anteriores.

* cascade = {PERSIST, MERGE}
* * */

/*
* HECHO
* FK A UBICACION: 1
*
* UBICACION:
* ID: 1
* FK A PAIS: 1
* FK A PROVINCIA: 1
*
* ID 2
* FK A PAIS: 1
* FK A PROVINCIA: 2
*
* */
