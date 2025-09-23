package modulos.agregacion.entities.DbMain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hecho_ref")
@Getter
@Setter

// NOTA: Entiendo que HechoRef va a ser necesario solo en las relaciones one to many de coleccion a hechos
public class HechoRef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuente", length = 20)
    private Fuente fuente;

    public HechoRef(Fuente fuente){
        this.fuente = fuente;
    }

    public HechoRef() {

    }
}
