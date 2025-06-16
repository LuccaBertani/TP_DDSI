package modulos.shared.dtos.input;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class VisualizarHechosInputDTO {
    @NotNull(message = "El id_usuario es obligatorio")
    long id_usuario;
    @NotNull(message = "El id_coleccion es obligatorio")
    long id_coleccion;
}
