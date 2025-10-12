package modulos.agregacion.entities.fuentes.Requests;

import lombok.Data;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;

import java.util.List;

@Data
public class HechosSkibidi {
    List<VisualizarHechosOutputDTO> hechos;
}
