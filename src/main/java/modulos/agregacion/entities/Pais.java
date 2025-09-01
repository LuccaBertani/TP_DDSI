package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pais")
public class Pais {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String pais;

    public Pais(){

    }
}

/*
* TABLA PAIS:
* ARGENTINA CORDOBA
* ARGENTINA CABA
* ARGENTINA SANTA FE
*
* TABLA PROVINCIA
* CORDOBA ARGENTINA
* WASHINGTON USA
* SAN LUIS ARGENTINA
* */


