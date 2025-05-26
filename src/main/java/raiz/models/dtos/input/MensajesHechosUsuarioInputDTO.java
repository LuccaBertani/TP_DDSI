package raiz.models.dtos.input;

import jakarta.validation.constraints.NotNull;

public class MensajesHechosUsuarioInputDTO {
    @NotNull(message = "el campo id_usuario es obligatorio")
    Long id_usuario;
}
