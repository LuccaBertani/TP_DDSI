package modulos.agregacion.entities.DbDinamica.solicitudes;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbMain.usuario.Usuario;

@Getter
@Setter
@Entity
@Table(name = "solicitudHecho")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_solicitud", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class SolicitudHecho {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_solicitudHecho_usuario"))
    protected Usuario usuario;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_hecho", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_solicitudHecho_hecho"))
    protected HechoDinamica hecho;
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    protected long id;
    @Column (name = "justificacion", length = 1000)
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
