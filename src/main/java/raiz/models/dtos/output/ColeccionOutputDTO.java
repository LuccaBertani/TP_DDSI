package raiz.models.dtos.output;

import lombok.Data;
import lombok.Setter;
import raiz.models.dtos.input.FiltroHechosDTO;

@Data
public class ColeccionOutputDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private FiltroHechosDTO filtros;
}
