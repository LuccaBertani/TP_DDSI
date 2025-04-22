import lombok.Getter;

@Getter
public class SolicitudHecho {
    private ContextoPersona GestorPersona;
    private Hecho hecho;

    public SolicitudHecho(ContextoPersona GestorPersona, Hecho hecho) {
        this.GestorPersona = GestorPersona;
        this.hecho = hecho;
    }
}
