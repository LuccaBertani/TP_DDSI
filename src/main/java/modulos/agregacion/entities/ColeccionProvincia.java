package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "coleccionProvincia")
public class ColeccionProvincia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_coleccion", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_coleccion_coleccionProvincia"))
    private Coleccion coleccion;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_provincia", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_provincia_coleccionProvincia"))
    private Provincia provincia;
    @Column(name = "cantidad_hechos")
    private Integer cantidad;

    @Column(name = "timestamp")
    private Instant timestamp;

    public ColeccionProvincia(Coleccion coleccion, Provincia provincia, Integer cantidad) {
        this.coleccion = coleccion;
        this.provincia = provincia;
        this.cantidad = cantidad;
    }

    public ColeccionProvincia() {

    }
}
