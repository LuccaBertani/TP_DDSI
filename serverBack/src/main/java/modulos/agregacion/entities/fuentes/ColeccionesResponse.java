package modulos.agregacion.entities.fuentes;

import lombok.Data;
import modulos.agregacion.entities.fuentes.Requests.ColeccionResponse;

import java.util.List;

@Data
public class ColeccionesResponse {
private List<ColeccionResponse> colecciones;
}
