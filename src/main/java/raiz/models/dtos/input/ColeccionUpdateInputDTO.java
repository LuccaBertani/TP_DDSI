package raiz.models.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import raiz.models.dtos.output.VisualizarHechosOutputDTO;

import java.util.List;

@Data
public class ColeccionUpdateInputDTO {
    @NotNull(message = "el id_coleccion es obligatorio")
    private Long id_coleccion;

    private String titulo;
    private String descripcion;

    //Criterios (es decir los filtros)
    private String categoria;
    private Integer contenidoMultimedia;
    private String fechaAcontecimientoInicial;
    private String fechaAcontecimientoFinal;
    private String fechaCargaInicial;
    private String fechaCargaFinal;
    private Integer origen;
    private String pais;
    private String descripcionFiltro;
    private String tituloFiltro;

    List<VisualizarHechosOutputDTO> hechos;

}
