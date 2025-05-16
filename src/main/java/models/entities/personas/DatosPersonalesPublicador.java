package models.entities.personas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatosPersonalesPublicador {
    private Long id;
    private String nombre; // Campo obligatorio si se quiere subir un hecho
    private String apellido;
    private Integer edad;
}