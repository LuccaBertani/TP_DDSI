package modulos.agregacion.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "sinonimo")
@Entity
public class Sinonimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sinonimo", length = 50)
    private String sinonimoStr;
}
