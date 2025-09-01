package modulos.agregacion.entities.solicitudes;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.usuario.Usuario;

@Getter
@Setter
@Entity
@Table(name = "solicitudHecho")
public class SolicitudHecho {
    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private Usuario usuario;
    @OneToOne
    @JoinColumn(name = "id_hecho", referencedColumnName = "id")
    private Hecho hecho;
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    @Column (name = "justificacion")
    private String justificacion;
    @Column (name = "procesada")
    private boolean procesada;
    @Column (name = "rechazadaPorSpam")
    private boolean rechazadaPorSpam;

    public SolicitudHecho(Usuario usuario, Hecho hecho) {
        this.usuario = usuario;
        this.hecho = hecho;
        this.procesada = false;
        this.rechazadaPorSpam = false;
    }

    public SolicitudHecho(Usuario usuario, Hecho hecho, String justificacion) {
        this(usuario, hecho);
        this.justificacion = justificacion;
    }

    public SolicitudHecho() {

    }
}
