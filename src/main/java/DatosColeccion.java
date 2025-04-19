import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatosColeccion {
    private String titulo;
    private String descripcion;
    private Fuente fuente;

    public DatosColeccion(String titulo, String descripcion, Fuente fuente) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuente = fuente;
    }
}
