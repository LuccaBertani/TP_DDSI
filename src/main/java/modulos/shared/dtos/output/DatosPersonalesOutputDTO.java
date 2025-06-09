package modulos.shared.dtos.output;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class DatosPersonalesOutputDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private Integer edad;
}
