package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioInputDTO {
    @NotNull(message = "Campo nombre obligatorio")
    private String nombre;
    @NotNull(message = "Campo apellido obligatorio")
    private String apellido;
    @NotNull(message = "Campo edad obligatorio")
    private Integer edad;
    @NotNull(message = "Campo contraseña obligatorio")
    private String contrasenia;
}
