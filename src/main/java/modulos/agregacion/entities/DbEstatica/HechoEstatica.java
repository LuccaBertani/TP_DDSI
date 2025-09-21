package modulos.agregacion.entities.DbEstatica;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.DbMain.Hecho;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "hecho_estatica")
@Setter
@Getter
public class HechoEstatica extends Hecho {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hecho_dataset",
            joinColumns = @JoinColumn(name = "hecho_id"),
            inverseJoinColumns = @JoinColumn(name = "dataset_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_hecho_dataset", columnNames = {"hecho_id","dataset_id"})
    )
    private List<Dataset> datasets;

    public HechoEstatica(){
        this.datasets = new ArrayList<>();
    }
}
