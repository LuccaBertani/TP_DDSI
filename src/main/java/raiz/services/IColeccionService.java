package raiz.services;

import raiz.models.dtos.input.ColeccionInputDTO;
import raiz.models.dtos.output.ColeccionOutputDTO;
import raiz.models.entities.Coleccion;
import raiz.models.entities.RespuestaHttp;

import java.util.List;

public interface IColeccionService {
    public RespuestaHttp<Void> crearColeccion(ColeccionInputDTO inputDTO);
    RespuestaHttp<List<ColeccionOutputDTO>> obtenerTodasLasColecciones();
}
