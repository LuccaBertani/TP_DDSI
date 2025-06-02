package raiz.services;
import raiz.models.dtos.input.FiltroHechosDTO;
import raiz.models.dtos.input.ImportacionHechosInputDTO;
import raiz.models.dtos.input.SolicitudHechoInputDTO;
import raiz.models.dtos.output.VisualizarHechosOutputDTO;
import raiz.models.entities.Hecho;
import raiz.models.entities.RespuestaHttp;

import java.util.List;

public interface IHechosService {
    public RespuestaHttp<Void> subirHecho(SolicitudHechoInputDTO dtoInput);
    public RespuestaHttp<Void> importarHechos(ImportacionHechosInputDTO dtoInput);
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(FiltroHechosDTO inputDTO);
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechos(Long id_coleccion);
    public void refrescarColeccionesCronjob();
    public RespuestaHttp<Void> refrescarColecciones(Long idUsuario);
    public void mapearHechosAColecciones(List<Hecho> hechos);
    public void mapearHechoAColecciones(Hecho hecho);
    public List<Hecho> getAllHechos();
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> navegarPorHechosProxyMetamapa();
}
