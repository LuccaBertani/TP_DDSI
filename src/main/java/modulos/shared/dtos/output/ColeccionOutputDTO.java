package modulos.shared.dtos.output;

import lombok.Data;
import modulos.shared.dtos.input.CriteriosColeccionDTO;


import java.util.List;

@Data
public class ColeccionOutputDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private CriteriosColeccionDTO criterios;
    private List<VisualizarHechosOutputDTO> hechos;
}
