package raiz.models.dtos.output;

import lombok.Data;
import lombok.Setter;
import raiz.models.dtos.input.CriteriosColeccionDTO;
import raiz.models.dtos.input.FiltroHechosDTO;

import java.util.List;

@Data
public class ColeccionOutputDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private CriteriosColeccionDTO criterios;
    private List<VisualizarHechosOutputDTO> hechos;
}
