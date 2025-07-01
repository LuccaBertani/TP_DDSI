package modulos.shared.dtos.output;


import lombok.Data;
import lombok.Setter;

@Data
public class VisualizarHechosOutputDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String pais;
    private String fechaAcontecimiento;
    private int contenidoMultimedia;
}
