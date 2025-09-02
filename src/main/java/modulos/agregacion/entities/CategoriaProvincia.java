package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categoriaProvincia")
public class CategoriaProvincia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_categoria", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_categoriaProvincia_categoria"))
    private Categoria categoria;
    @ManyToOne
    @JoinColumn(name = "id_provincia", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_categoriaProvincia_provincia"))
    private Provincia provincia;
    @Column(name = "cantidad_hechos")
    private Integer cantidad;

    public CategoriaProvincia(Categoria categoria,Provincia provincia, Integer cantidad){
        this.categoria = categoria;
        this.provincia = provincia;
        this.cantidad = cantidad;
    }

    public CategoriaProvincia() {

    }
}
