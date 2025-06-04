package raiz.services;

import jakarta.validation.Valid;
import raiz.models.dtos.input.ColeccionInputDTO;
import raiz.models.dtos.output.ColeccionOutputDTO;
import raiz.models.entities.Coleccion;
import raiz.models.entities.RespuestaHttp;

import java.util.List;

public interface IColeccionService {
    RespuestaHttp<Void> crearColeccion(ColeccionInputDTO inputDTO);
    RespuestaHttp<List<ColeccionOutputDTO>> obtenerTodasLasColecciones();
    RespuestaHttp<ColeccionOutputDTO> getColeccion(Long id_coleccion);
    RespuestaHttp<ColeccionOutputDTO> deleteColeccion(Long id_coleccion);
}
