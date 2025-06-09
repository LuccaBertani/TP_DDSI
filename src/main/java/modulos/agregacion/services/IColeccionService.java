package modulos.agregacion.services;

import jakarta.validation.Valid;
import modulos.shared.dtos.input.ColeccionInputDTO;
import modulos.shared.dtos.input.ColeccionUpdateInputDTO;
import modulos.shared.dtos.output.ColeccionOutputDTO;
import modulos.shared.RespuestaHttp;

import java.util.List;

public interface IColeccionService {
    RespuestaHttp<Void> crearColeccion(ColeccionInputDTO inputDTO);
    RespuestaHttp<List<ColeccionOutputDTO>> obtenerTodasLasColecciones();
    RespuestaHttp<ColeccionOutputDTO> getColeccion(Long id_coleccion);
    RespuestaHttp<ColeccionOutputDTO> deleteColeccion(Long id_coleccion);

    RespuestaHttp<Void> agregarFuente(@Valid Long idColeccion, @Valid String dataSet);

    RespuestaHttp<Void> eliminarFuente(@Valid Long idColeccion, @Valid String dataSet);

    RespuestaHttp<Void> updateColeccion(@Valid ColeccionUpdateInputDTO dto);
}
