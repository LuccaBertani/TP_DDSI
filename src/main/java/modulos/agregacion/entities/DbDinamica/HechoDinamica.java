package modulos.agregacion.entities.DbDinamica;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import modulos.agregacion.entities.DbMain.Hecho;

@Entity
@Table(name = "hecho_dinamica")
public class HechoDinamica extends Hecho {
    public HechoDinamica() {
    }
}


