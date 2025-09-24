package modulos.servicioEstadistica.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import modulos.agregacion.entities.DbMain.Coleccion;
import modulos.agregacion.entities.DbMain.Provincia;

import java.time.Instant;

@Data
@Entity
@Table(name = "coleccion_provincia")
@AllArgsConstructor
public class ColeccionProvincia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coleccion_id")
    private Long coleccion_id;
    @Column(name = "provincia_id")
    private Long provincia_id;
    @Column(name = "cantidad_hechos")
    private Integer cantidad;

    @Column(name = "timestamp")
    private Instant timestamp;

    public ColeccionProvincia(Long coleccion_id, Long provincia_id, Integer cantidad) {
        this.coleccion_id = coleccion_id;
        this.provincia_id = provincia_id;
        this.cantidad = cantidad;
        this.timestamp = Instant.now();
    }

    public ColeccionProvincia() {
    }
}
