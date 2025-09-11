package modulos.agregacion.entities;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_categoriaProvincia_categoria"))
    private Categoria categoria;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_provincia", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_categoriaProvincia_provincia"))
    private Provincia provincia;
    @Column(name = "cantidad_hechos")
    private Integer cantidad;

    @Column(name = "timestamp")
    private Instant timestamp;

    public CategoriaProvincia(Categoria categoria,Provincia provincia, Integer cantidad){
        this.categoria = categoria;
        this.provincia = provincia;
        this.cantidad = cantidad;
    }

    public CategoriaProvincia() {

    }
}
