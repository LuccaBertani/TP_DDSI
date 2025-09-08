package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;

import java.util.List;

@Data
public class ColeccionUpdateInputDTO {
    @NotNull(message = "el id_coleccion es obligatorio")
    private Long id_coleccion;
    @NotNull(message = "el usuario es dawn")
    private Long id_usuario;

    private String titulo;
    private String descripcion;
    private Boolean reemplazarHechos;
    //Criterios (es decir los filtros)
    private Long categoria;
    private Integer contenidoMultimedia;
    private String fechaAcontecimientoInicial;
    private String fechaAcontecimientoFinal;
    private String fechaCargaInicial;
    private String fechaCargaFinal;
    private Integer origen;
    private Long pais;
    private Long provincia;
    private String descripcionFiltro;
    private String tituloFiltro;

    List<VisualizarHechosOutputDTO> hechos;

}
