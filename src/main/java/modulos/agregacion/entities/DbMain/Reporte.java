package modulos.agregacion.entities.DbMain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.DbMain.hechoRef.HechoRef;

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
    @JoinColumns({
            @JoinColumn(name = "hecho_id",     referencedColumnName = "id"),     // 1º id
            @JoinColumn(name = "hecho_fuente", referencedColumnName = "fuente")  // 2º fuente
    })
    HechoRef hecho_asociado;

    public Reporte(String motivo, HechoRef hecho_asociado){
        this.motivo = motivo;
        this.hecho_asociado = hecho_asociado;
    }

    public Reporte() {

    }
}
