package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;

import java.util.List;

@Data
public class ColeccionUpdateInputDTO {
    @NotNull(message = "el id_coleccion es obligatorio")
    private Long id_coleccion;
    @NotNull(message = "el usuario es obligatorio")
    private Long id_usuario;

    private String titulo;

    private String descripcion;

    private CriteriosColeccionDTO criterios;
    // No agrego algoritmo de consenso porque para eso está el otro méto do
}
