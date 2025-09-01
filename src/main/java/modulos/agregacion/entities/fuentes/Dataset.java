package modulos.agregacion.entities.fuentes;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dataset")
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fuente", nullable = false)
    private String fuente;

    protected Dataset() { }

    public Dataset(String fuente) {
        this.fuente = fuente;
    }
}