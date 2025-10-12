package modulos.agregacion.entities.fuentes;

import lombok.Data;
import modulos.agregacion.entities.fuentes.Requests.HechoResponseMetamapa;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;

import java.util.List;

@Data
public class HechosMetamapaResponse {
List<HechoResponseMetamapa> hechos;
}
