package modulos.agregacion.entities.DbDinamica;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;
import modulos.agregacion.entities.DbMain.Hecho;

@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "hecho_dinamica")
public class HechoDinamica extends Hecho {
    public HechoDinamica() {
    }
}


