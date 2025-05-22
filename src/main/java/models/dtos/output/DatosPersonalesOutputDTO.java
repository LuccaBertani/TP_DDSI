package models.dtos.output;

import lombok.Data;
import lombok.Setter;

@Setter
@Data
public class DatosPersonalesOutputDTO {
    private List<Merca> usuarios;
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
