package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

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

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "categoria_id")
    private List<Sinonimo> sinonimos;

    public Categoria(){
        sinonimos = new ArrayList<>();
    }
}

// TODO: TESTEAR TODOS LOS ENDPOINTS (?
// TODO  HACER EL PUNTO EXTRA!!
// TODO testear y corregir si es necesario el endpoint importarHechos
// TODO testear y corregir si es necesario endpoints de SolicitarHechoController