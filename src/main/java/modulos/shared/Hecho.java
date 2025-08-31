package modulos.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.fuentes.Dataset;
import modulos.shared.dtos.AtributosHecho;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "hecho")
public class Hecho {

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //FK a la tabla de usuarios
    private Long id_usuario;

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
        this.id_usuario=-1L;
        this.datasets = new ArrayList<>();
        //this.atributosHecho = atributosHecho;
    }
}
