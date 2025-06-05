package raiz.models.entities;

import lombok.Getter;

@Getter
public class Reporte {
    String motivo;
    Long id;
    Long id_hecho;

    public Reporte(String motivo, long id, Long id_hecho){
        this.motivo = motivo;
        this.id = id;
        this.id_hecho = id_hecho;
    }

}
