package modulos.Front.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImportacionHechosInputDTO {
    @NotNull(message = "La fuente es obligatoria")
    String fuenteString;
}
