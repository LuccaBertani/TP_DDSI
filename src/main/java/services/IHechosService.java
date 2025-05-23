package services;
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
    public RespuestaHttp<Integer> subirHecho(SolicitudHechoInputDTO dtoInput);
    public RespuestaHttp<Integer> importarHechos(ImportacionHechosInputDTO dtoInput);
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(List<String> filtros, Long id_coleccion);
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(Long id_coleccion);
}
