package modulos.agregacion.entities.DbMain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_hecho_asociado", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_reporte_hecho_ref"))
    HechoRef hecho_asociado;

    public Reporte(String motivo, HechoRef hecho_asociado){
        this.motivo = motivo;
        this.hecho_asociado = hecho_asociado;
    }

    public Reporte() {

    }
}
