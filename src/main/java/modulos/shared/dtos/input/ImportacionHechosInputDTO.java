package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImportacionHechosInputDTO {
    @NotNull(message = "El id del usuario es obligatorio")
    Long id_usuario;
    @NotNull(message = "La fuente es obligatoria")
    String fuenteString;
}
