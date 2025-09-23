package modulos.agregacion.entities.DbDinamica.solicitudes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.DbMain.Hecho;

@Getter
@Setter
@Entity
@Table(name = "reporte")
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "motivo")
    String motivo;

    @Column(name = "hecho_asociado_id")
    Long hecho_asociado_id;

    public Reporte(String motivo, Long hecho_asociado_id){
        this.motivo = motivo;
        this.hecho_asociado_id = hecho_asociado_id;
    }

    public Reporte() {

    }
}
