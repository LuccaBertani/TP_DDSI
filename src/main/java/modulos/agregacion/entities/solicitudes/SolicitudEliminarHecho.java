package modulos.agregacion.entities.solicitudes;

import modulos.agregacion.entities.HechoDinamica;
import modulos.agregacion.entities.usuario.Usuario;

public class SolicitudEliminarHecho extends SolicitudHecho{
    public SolicitudEliminarHecho(Usuario usuario, HechoDinamica hecho) {
        this.usuario = usuario;
        this.hecho = hecho;
        this.procesada = false;
        this.rechazadaPorSpam = false;
    }

    public SolicitudEliminarHecho(Usuario usuario, HechoDinamica hecho, String justificacion) {
        this(usuario, hecho);
        this.justificacion = justificacion;
    }
}
