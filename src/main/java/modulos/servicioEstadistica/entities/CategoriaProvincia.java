package modulos.servicioEstadistica.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "categoriaProvincia")
public class CategoriaProvincia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "categoria_id")
    private Long categoria_id;
    @Column(name = "provincia_id")
    private Long provincia_id;
    @Column(name = "cantidad_hechos")
    private Integer cantidad;

    @Column(name = "timestamp")
    private Instant timestamp;

    public CategoriaProvincia(Long id, Long categoria_id, Long provincia_id, Integer cantidad, Instant timestamp) {
        this.id = id;
        this.categoria_id = categoria_id;
        this.provincia_id = provincia_id;
        this.cantidad = cantidad;
        this.timestamp = timestamp;
    }

    public CategoriaProvincia() {

    }
}
