package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Data;
import modulos.shared.Pais;

@Data
@Entity
@Table(
        name = "ubicacion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pais_provincia",     // nombre de la constraint en DB
                        columnNames = {"pais_id", "provincia_id"}
                )
        }
)
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "provincia_id")
    private Provincia provincia;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;
}