package modulos.solicitudes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.shared.Hecho;

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

    @ManyToOne
    @JoinColumn(name = "hecho_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_reporte_hecho"))
    Hecho hecho_asociado;

    public Reporte(String motivo, Hecho hecho){
        this.motivo = motivo;
        this.hecho_asociado = hecho;
    }

    public Reporte() {

    }
}
