package modulos.agregacion.entities.fuentes;

import lombok.Data;
import modulos.agregacion.entities.fuentes.Requests.ColeccionResponse;
import modulos.shared.dtos.output.ColeccionOutputDTO;

import java.util.List;

@Data
public class ColeccionesResponse {
private List<ColeccionOutputDTO> colecciones;
}
