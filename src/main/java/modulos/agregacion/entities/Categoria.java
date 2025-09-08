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

//TODO hacer que esto lo haga sql + actualizar todos los services con lo de sql
// TODO: Filtros con SQL HECHO
// TODO: Normalización para categorías y provincias HECHO
// TODO: Controllers para crear categorías por parte de los administradores HECHO
// TODO: Que los buscadores NO CREEN los objeto s SINO LOS ADMINISTRADORES CON UN ENDPOINT "CREAR CATEGORIA O PAIS" HECHO
// TODO: TESTEAR TODOS LOS ENDPOINTS (?
// TODO  HACER EL PUNTO EXTRA!!