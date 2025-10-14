package modulos.agregacion.entities.fuentes.Responses;

import lombok.Data;
import modulos.agregacion.entities.atributosHecho.ContenidoMultimedia;

import java.util.List;

@Data
public class HechoMetamapaResponse {
    private Long id;
    private String fuente;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String pais;
    private String provincia;
    private String fechaAcontecimiento;
    private Double latitud;
    private Double longitud;
    private List<ContenidoMultimedia> contenido;
}
