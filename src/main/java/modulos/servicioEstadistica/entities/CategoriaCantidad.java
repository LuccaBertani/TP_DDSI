package modulos.servicioEstadistica.entities;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "categoriaCantidad")
public class CategoriaCantidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "categoria_id")
    private Long categoria_id;

    @Column(name = "cantidad_hechos")
    private Integer cantidad;

    public CategoriaCantidad(Long id, Long categoria_id, Integer cantidad) {
        this.id = id;
        this.categoria_id = categoria_id;
        this.cantidad = cantidad;
    }


    public CategoriaCantidad() {

    }
}
