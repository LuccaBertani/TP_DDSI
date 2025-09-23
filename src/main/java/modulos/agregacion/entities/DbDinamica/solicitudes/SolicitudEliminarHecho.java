package modulos.agregacion.entities.DbDinamica.solicitudes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbMain.usuario.Usuario;
@Entity
@DiscriminatorValue("ELIMINAR")
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

    public SolicitudEliminarHecho() {

    }
}
