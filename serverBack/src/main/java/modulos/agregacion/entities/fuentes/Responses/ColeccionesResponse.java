package modulos.agregacion.entities.fuentes.Responses;

import lombok.Data;
import modulos.shared.dtos.output.ColeccionOutputDTO;

import java.util.List;

@Data
public class ColeccionesResponse {
private List<ColeccionOutputDTO> colecciones;
}
