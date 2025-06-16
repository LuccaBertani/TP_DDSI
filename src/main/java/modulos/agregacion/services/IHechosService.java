package modulos.agregacion.services;
import modulos.shared.dtos.input.FiltroHechosDTO;
import modulos.shared.dtos.input.GetHechosColeccionInputDTO;
import modulos.shared.dtos.input.ImportacionHechosInputDTO;
import modulos.shared.dtos.input.SolicitudHechoInputDTO;
import modulos.shared.dtos.output.VisualizarHechosOutputDTO;
import modulos.shared.Hecho;
import modulos.shared.RespuestaHttp;

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
    public RespuestaHttp<List<VisualizarHechosOutputDTO>> getHechosColeccion(GetHechosColeccionInputDTO inputDTO);
}
