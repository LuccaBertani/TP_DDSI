package modulos.agregacion.entities.solicitudes;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.HechoDinamica;
import modulos.agregacion.entities.usuario.Usuario;

@Getter
@Setter
@Entity
@Table(name = "solicitudHecho")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_solicitud", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class SolicitudHecho {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    protected Usuario usuario;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_hecho", referencedColumnName = "id")
    protected HechoDinamica hecho;
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    protected long id;
    @Column (name = "justificacion")
    protected String justificacion;
    @Column (name = "procesada")
    protected boolean procesada;
    @Column (name = "rechazadaPorSpam")
    protected boolean rechazadaPorSpam;

    public SolicitudHecho(Usuario usuario, HechoDinamica hecho) {
        this.usuario = usuario;
        this.hecho = hecho;
        this.procesada = false;
        this.rechazadaPorSpam = false;
    }

    public SolicitudHecho(Usuario usuario, HechoDinamica hecho, String justificacion) {
        this(usuario, hecho);
        this.justificacion = justificacion;
    }

    public SolicitudHecho() {

    }
}
