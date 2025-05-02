package models.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatosPersonalesPublicador {
    //TODO
    private Long id; // Hay que hacer un setter del id que sea el length de la lista de la memoria de datos personales
    private String nombre; // Campo obligatorio si se quiere subir un hecho
    private String apellido;
    private Integer edad;
}
