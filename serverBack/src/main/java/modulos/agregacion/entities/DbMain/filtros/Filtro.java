package modulos.agregacion.entities.DbMain.filtros;

import jakarta.persistence.*;
import lombok.Getter;
import modulos.agregacion.entities.DbMain.Hecho;

@Entity
@Table(name = "filtro")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
public abstract class Filtro implements IFiltro{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public Boolean aprobarHecho(Hecho hecho) {
        return null;
    }

}
