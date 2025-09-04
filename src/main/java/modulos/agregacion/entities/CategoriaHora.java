package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categoriaHora")
public class CategoriaHora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_categoria", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_categoria_categoriaHora"))
    private Categoria categoria;
    @Column(name = "hora")
    private Integer hora;
    @Column(name = "cantidad_hechos")
    private Integer cantidad;

    public CategoriaHora(Categoria categoria, Integer hora, Integer cantidad){
        this.categoria = categoria;
        this.hora = hora;
        this.cantidad = cantidad;
    }

    public CategoriaHora() {

    }
}

/*
* CATEGORIA HORA
* ID 1 INCENDIO 4:00 100
* ID 2 INCENDIO 5:00 200
* */
