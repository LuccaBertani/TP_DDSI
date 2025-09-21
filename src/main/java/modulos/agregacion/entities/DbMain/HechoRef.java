package modulos.agregacion.entities.DbMain;

import jakarta.persistence.*;

@Entity
@Table(name = "hecho_ref")
public class HechoRef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuente", length = 20)
    private Fuente fuente;

}
