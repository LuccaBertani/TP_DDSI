package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", length = 50)
    private String titulo;

    @OneToMany
    @JoinColumn(name = "categoria_id")
    private List<Sinonimo> sinonimos;

    public Categoria(){
        sinonimos = new ArrayList<>();
    }
}