package modulos.agregacion.entities.fuentes.Requests;

import lombok.Data;
import modulos.agregacion.entities.DbMain.Hecho;

import java.util.List;

@Data
public class HechosResponse {
    private List<HechoResponse> hechos;
}
