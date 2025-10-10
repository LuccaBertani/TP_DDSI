package modulos.agregacion.entities.DbDinamica.solicitudes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbMain.usuario.Usuario;
@Entity
@DiscriminatorValue("ELIMINAR")
public class SolicitudEliminarHecho extends SolicitudHecho{
    public SolicitudEliminarHecho(Long usuario_id, HechoDinamica hecho) {
        this.usuario_id = usuario_id;
        this.hecho = hecho;
        this.procesada = false;
        this.rechazadaPorSpam = false;
    }

    public SolicitudEliminarHecho(Long usuario_id, HechoDinamica hecho, String justificacion) {
        this(usuario_id, hecho);
        this.justificacion = justificacion;
    }

    public SolicitudEliminarHecho() {

    }
}
