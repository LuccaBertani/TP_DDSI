package services;
import models.dtos.input.ImportacionHechosInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.fuentes.Fuente;
import models.entities.personas.Usuario;

import java.util.List;

public interface IHechosService {
    public RespuestaHttp<Integer> subirHecho(SolicitudHechoInputDTO dtoInput);
    public RespuestaHttp<Integer> importarHechos(ImportacionHechosInputDTO dtoInput);
    public void navegarPorHechos(List<Filtro> filtros, Coleccion coleccion);
    public void navegarPorHechos(Coleccion coleccion);
}
