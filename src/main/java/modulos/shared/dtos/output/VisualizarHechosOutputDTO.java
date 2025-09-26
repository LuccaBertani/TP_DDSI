package modulos.shared.dtos.output;


import lombok.Data;
import lombok.Setter;

@Data
public class VisualizarHechosOutputDTO {
    private Long id;
    private String fuente;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String pais;
    private String provincia;
    private Long id_categoria;
    private Long id_pais;
    private Long id_provincia;
    private String fechaAcontecimiento;
    private int contenidoMultimedia;
    private Double latitud;
    private Double longitud;
}
