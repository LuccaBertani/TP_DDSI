import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Coleccion {
    @Getter
    @Setter
    private String titulo;
    @Getter
    @Setter
    private String descripcion;

    public Coleccion(DatosColeccion datosColeccion) {
        this.titulo = datosColeccion.getTitulo();
        this.descripcion = datosColeccion.getDescripcion();
        this.fuente = datosColeccion.getFuente();
    }

    private List<Hecho> hechos;
    private Fuente fuente;
    private List<Filtro> criterio;



}
