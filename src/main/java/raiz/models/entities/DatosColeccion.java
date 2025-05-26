package raiz.models.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatosColeccion {
    private String titulo;
    private String descripcion;
    private String fuente;

    public DatosColeccion(String titulo, String descripcion, String fuente) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuente = fuente;
    }
}
