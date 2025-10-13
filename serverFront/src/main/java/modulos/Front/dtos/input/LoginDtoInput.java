package modulos.Front.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDtoInput {
    @NotNull(message = "Ingrese contraseña")
    private String contrasenia;
    @NotNull(message = "Ingrese nombre de usuario")
    private String nombreDeUsuario;
}
