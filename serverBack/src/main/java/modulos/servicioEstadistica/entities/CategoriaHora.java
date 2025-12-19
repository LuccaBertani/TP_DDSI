package modulos.servicioEstadistica.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "categoria_hora")
public class CategoriaHora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "categoria_id")
    private Long categoria_id;
    @Column(name = "hora")
    private Integer hora;
    @Column(name = "cantidad_hechos")
    private Integer cantidad;

    @Column(name = "timestamp")
    private Instant timestamp;

    public CategoriaHora(Long categoria_id, Integer hora, Integer cantidad){
        this.categoria_id = categoria_id;
        this.hora = hora;
        this.cantidad = cantidad;
        this.timestamp = Instant.now();
    }

    public CategoriaHora() {

    }
}
