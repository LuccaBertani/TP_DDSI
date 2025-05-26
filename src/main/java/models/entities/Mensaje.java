package models.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mensaje {
    Long id;
    Long id_solicitud_hecho;
    Long id_receptor;
    String textoMensaje;

    public Mensaje(Long solicitudHecho, Long receptor, String textoMensaje){
        this.id_solicitud_hecho=solicitudHecho;
        this.id_receptor=receptor;
        this.textoMensaje=textoMensaje;
    }
}