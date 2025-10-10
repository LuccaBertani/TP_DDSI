package modulos.agregacion.entities.fuentes.Requests;

import lombok.Data;

@Data
public class HechoResponse {
    private String titulo;
    private String descripcion;
    private String categoria;
    private Double latitud;
    private double longitud;
    private String fecha_hecho;
    private String created_at;
    private String updated_at;
}
