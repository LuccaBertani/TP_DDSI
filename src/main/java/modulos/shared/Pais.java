package modulos.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Provincia;

import java.util.ArrayList;
import java.util.List;

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


