package services;
import models.dtos.input.FiltroHechosDTO;
import models.dtos.input.ImportacionHechosInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.dtos.input.VisualizarHechosInputDTO;
import models.dtos.output.VisualizarHechosOutputDTO;
import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.fuentes.Fuente;
import models.entities.personas.Usuario;

import java.util.List;

public interface IHechosService {
    public RespuestaHttp<Void> subirHecho(SolicitudHechoInputDTO dtoInput);
    public RespuestaHttp<Void> importarHechos(ImportacionHechosInputDTO dtoInput);
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(FiltroHechosDTO inputDTO);
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(Long id_coleccion);
}
