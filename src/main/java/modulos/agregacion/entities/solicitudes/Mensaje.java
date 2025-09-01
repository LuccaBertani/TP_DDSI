package modulos.agregacion.entities.solicitudes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.usuario.Usuario;

@Getter
@Setter
@Entity
@Table (name = "mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "solicitud_hecho_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_solicitud_hecho"))
    SolicitudHecho solicitud_hecho;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario_id"))
    Usuario receptor;

    @Column (name = "texto")
    String textoMensaje;

    public Mensaje(SolicitudHecho solicitudHecho, Usuario receptor, String textoMensaje){
        this.solicitud_hecho=solicitudHecho;
        this.receptor=receptor;
        this.textoMensaje=textoMensaje;
    }

    public Mensaje() {

    }
}