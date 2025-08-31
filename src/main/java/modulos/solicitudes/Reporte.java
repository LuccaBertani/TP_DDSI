package modulos.solicitudes;

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
    @Column(name = "id_hecho")

    @OneToOne
    @JoinColumn(name = "id_hecho", referencedColumnName = "id")
    Long id_hecho;

    public Reporte(String motivo, Long id_hecho){
        this.motivo = motivo;
        this.id_hecho = id_hecho;
    }

    public Reporte() {

    }
}
