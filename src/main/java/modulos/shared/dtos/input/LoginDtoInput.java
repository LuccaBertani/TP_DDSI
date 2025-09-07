package modulos.shared.dtos.input;

import lombok.Data;

@Data
public class LoginDtoInput {
    private String contrasenia;
    private String nombreDeUsuario;
}
