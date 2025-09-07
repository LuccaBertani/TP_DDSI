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

    @Column(name = "sinonimo", length = 50, columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci")
    private String sinonimoStr;

    public Sinonimo(String sinonimoStr){
        this.sinonimoStr = sinonimoStr;
    }

    public Sinonimo(){

    }
}