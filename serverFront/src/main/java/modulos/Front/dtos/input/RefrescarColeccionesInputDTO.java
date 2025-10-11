package modulos.Front.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefrescarColeccionesInputDTO {
    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;
}
