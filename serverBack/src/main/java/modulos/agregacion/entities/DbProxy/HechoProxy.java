package modulos.agregacion.entities.DbProxy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;
import modulos.agregacion.entities.DbMain.Hecho;

@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "hecho_proxy")
public class HechoProxy extends Hecho {
    public HechoProxy() {

    }
}
