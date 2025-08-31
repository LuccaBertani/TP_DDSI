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
    Long id_solicitud_hecho;
    Long id_receptor;
    String textoMensaje;

    public Mensaje(Long solicitudHecho, Long receptor, String textoMensaje){
        this.id_solicitud_hecho=solicitudHecho;
        this.id_receptor=receptor;
        this.textoMensaje=textoMensaje;
    }

    public Mensaje() {

    }
}