package modulos.agregacion.entities.DbMain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class CantSolicitudesEliminacionSpam {
    @Column(name = "cantSolicitudesEliminacionSpam")
    private Integer cantSolicitudesEliminacionSpam;

    public CantSolicitudesEliminacionSpam(Integer cantSolicitudesEliminacionSpam){
        this.cantSolicitudesEliminacionSpam = cantSolicitudesEliminacionSpam;
    }

    public CantSolicitudesEliminacionSpam() {

    }
}
