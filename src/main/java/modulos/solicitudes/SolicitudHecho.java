package modulos.solicitudes;


import lombok.Getter;
import lombok.Setter;
import modulos.shared.Hecho;
import modulos.usuario.Usuario;

@Getter
@Setter
public class SolicitudHecho {
    private Usuario usuario;
    private Hecho hecho;
    private long id;
    private String justificacion;
    private boolean procesada;
    private boolean rechazadaPorSpam;

    public SolicitudHecho(Usuario usuario, Hecho hecho, long id) {
        this.usuario = usuario;
        this.hecho = hecho;
        this.id = id;
        this.procesada = false;
        this.rechazadaPorSpam = false;
    }

    public SolicitudHecho(Usuario usuario, Hecho hecho, long id, String justificacion) {
        this(usuario, hecho, id);
        this.justificacion = justificacion;
    }
}
