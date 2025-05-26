package raiz.models.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;


@Data
public class SolicitudHechoEliminarInputDTO {
    @NotNull(message = "El id_usuario es obligatorio")
    Long id_usuario;
    @NotNull(message = "El id_hecho es obligatorio")
    Long id_hecho;
}
