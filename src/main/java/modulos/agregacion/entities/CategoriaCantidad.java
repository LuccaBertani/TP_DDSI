package modulos.agregacion.entities;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "categoriaCantidad")
public class CategoriaCantidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_categoria", referencedColumnName = "id", nullable = false,
            foreignKey = @jakarta.persistence.ForeignKey(name = "fk_categoriaCantidad_categoria"))
    private Categoria categoria;

    @Column(name = "cantidad_hechos")
    private Integer cantidad;

    public CategoriaCantidad(Categoria categoria, Integer cantidad){
        this.categoria = categoria;
        this.cantidad = cantidad;
    }

    public CategoriaCantidad() {

    }
}
