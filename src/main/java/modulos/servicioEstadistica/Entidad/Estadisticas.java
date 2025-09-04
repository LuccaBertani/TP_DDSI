package modulos.servicioEstadistica.Entidad;
import jakarta.persistence.*;
import lombok.Data;
import modulos.agregacion.entities.*;

import java.util.ArrayList;
import java.util.List;
@Data
@Entity
@Table(name = "estadisticas")
public class Estadisticas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CantSolicitudesEliminacionSpam cantidadDeSpam;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categoriaCantidad_id", referencedColumnName = "id")
    CategoriaCantidad categoriaCantidad;

    @OneToMany
    @JoinColumn(name = "categoriaHora_id")
    List<CategoriaHora> categoriaHoras;

    @OneToMany
    @JoinColumn(name = "categoriaProvincia_id")
    List<CategoriaProvincia> categoriaProvincias;

    @OneToMany
    @JoinColumn(name = "coleccionProvincias_id")
    List<ColeccionProvincia> coleccionProvincias;

    public Estadisticas(CantSolicitudesEliminacionSpam cantidadDeSpam, CategoriaCantidad categoriaCantidad, List<CategoriaHora> categoriaHoras, List<CategoriaProvincia> categoriaProvincias, List<ColeccionProvincia> coleccionProvincias) {
        this.cantidadDeSpam = cantidadDeSpam;
        this.categoriaCantidad = categoriaCantidad;
        this.categoriaHoras = categoriaHoras;
        this.categoriaProvincias = categoriaProvincias;
        this.coleccionProvincias = coleccionProvincias;
    }

    public Estadisticas() {
        this.categoriaProvincias = new ArrayList<>();
        this.categoriaHoras = new ArrayList<>();
        this.coleccionProvincias = new ArrayList<>();
    }
}

/*
*Estadistica
*ID CANT_SPAM
*
* */
