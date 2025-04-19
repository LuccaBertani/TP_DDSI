import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Coleccion {

    private String titulo;
    private String descripcion;
    private List<Hecho> hechos = new ArrayList<>();
    private Fuente fuente;
    private List<Filtro> criterio = new ArrayList<>();

    public Coleccion(DatosColeccion datosColeccion) {
        this.titulo = datosColeccion.getTitulo();
        this.descripcion = datosColeccion.getDescripcion();
        this.fuente = datosColeccion.getFuente();
    }
}
