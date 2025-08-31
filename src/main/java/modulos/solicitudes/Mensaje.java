package modulos.solicitudes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (name = "mensaje")
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column (name = "id_solicitud")
    Long id_solicitud_hecho;

    @Column (name = "id_usuario")
    Long id_receptor;

    @Column (name = "texto")
    String textoMensaje;

    public Mensaje(Long solicitudHecho, Long receptor, String textoMensaje){
        this.id_solicitud_hecho=solicitudHecho;
        this.id_receptor=receptor;
        this.textoMensaje=textoMensaje;
    }

    public Mensaje() {

    }
}