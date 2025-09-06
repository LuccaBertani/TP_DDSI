package modulos.agregacion.entities.solicitudes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import modulos.agregacion.entities.HechoDinamica;
import modulos.agregacion.entities.usuario.Usuario;
@Entity
@DiscriminatorValue("MODIFICAR")
public class SolicitudModificarHecho extends SolicitudHecho{
    public SolicitudModificarHecho(Usuario usuario, HechoDinamica hecho) {
        this.usuario = usuario;
        this.hecho = hecho;
        this.procesada = false;
        this.rechazadaPorSpam = false;
    }

    public SolicitudModificarHecho(Usuario usuario, HechoDinamica hecho, String justificacion) {
        this(usuario, hecho);
        this.justificacion = justificacion;
    }

    public SolicitudModificarHecho() {

    }
}
