package modulos.agregacion.entities.filtros;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.fuentes.Origen;
@Getter
@Setter
@Table(name = "filtro_origen")
@Entity
public class FiltroOrigen extends Filtro {
    @Enumerated(EnumType.ORDINAL)
    private Origen origenDeseado;

    public FiltroOrigen(Origen origenDeseado){
        this.origenDeseado = origenDeseado;
    }

    public FiltroOrigen() {

    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getAtributosHecho().getOrigen().equals(origenDeseado);
    }
}
