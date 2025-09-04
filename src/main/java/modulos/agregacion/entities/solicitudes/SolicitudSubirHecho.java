package modulos.agregacion.entities.solicitudes;

import modulos.agregacion.entities.HechoDinamica;
import modulos.agregacion.entities.usuario.Usuario;

public class SolicitudSubirHecho extends SolicitudHecho{
    public SolicitudSubirHecho(Usuario usuario, HechoDinamica hecho) {
        this.usuario = usuario;
        this.hecho = hecho;
        this.procesada = false;
        this.rechazadaPorSpam = false;
    }

    public SolicitudSubirHecho(Usuario usuario, HechoDinamica hecho, String justificacion) {
        this(usuario, hecho);
        this.justificacion = justificacion;
    }
}
