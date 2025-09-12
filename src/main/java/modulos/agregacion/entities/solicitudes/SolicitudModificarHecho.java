package modulos.agregacion.entities.solicitudes;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import modulos.agregacion.entities.AtributosHechoModificar;
import modulos.agregacion.entities.HechoDinamica;
import modulos.agregacion.entities.usuario.Usuario;
@Entity
@DiscriminatorValue("MODIFICAR")
@Data
public class SolicitudModificarHecho extends SolicitudHecho{
    public SolicitudModificarHecho(Usuario usuario, HechoDinamica hecho) {
        this.usuario = usuario;
        this.hecho = hecho;
        this.procesada = false;
        this.rechazadaPorSpam = false;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private AtributosHechoModificar atributosAshei;

    public SolicitudModificarHecho(Usuario usuario, HechoDinamica hecho, String justificacion) {
        this(usuario, hecho);
        this.justificacion = justificacion;
    }

    public SolicitudModificarHecho() {
    }
}


