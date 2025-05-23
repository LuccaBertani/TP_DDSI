package models.dtos.input;

import lombok.Data;

@Data
public class UsuarioInputDTO {
    private String nombre;
    private String apellido;
    private Integer edad;
    private String contrasenia;
}
