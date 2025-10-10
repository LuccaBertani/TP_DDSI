package modulos.agregacion.entities.DbMain.filtros;

import jakarta.persistence.*;
import modulos.agregacion.entities.DbMain.Hecho;

@Entity
@Table(name = "filtro")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Filtro implements IFiltro{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tablas por subclase + join por PK
    private Long id;

    @Override
    public Boolean aprobarHecho(Hecho hecho) {
        return null;
    }

}
