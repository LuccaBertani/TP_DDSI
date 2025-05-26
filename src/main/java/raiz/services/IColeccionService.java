package raiz.services;

import raiz.models.dtos.input.ColeccionInputDTO;
import raiz.models.entities.RespuestaHttp;

public interface IColeccionService {
    public RespuestaHttp<Void> crearColeccion(ColeccionInputDTO inputDTO);
}
