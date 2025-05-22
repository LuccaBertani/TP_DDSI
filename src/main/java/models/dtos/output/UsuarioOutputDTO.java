package models.dtos.output;

import lombok.Data;
import lombok.Setter;

@Setter
@Data
public class UsuarioOutputDTO {
    private Long id;
    private String nombre;
    private String contrasenia;
    private Integer rol; // 0 1 2
}
