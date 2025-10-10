package modulos.agregacion.entities.DbMain;

import lombok.Getter;
import modulos.agregacion.entities.atributosHecho.ContenidoMultimedia;

import java.util.List;


//TODO CLASE JUBILADA!! YA ESTA HABILITADA PARA SER GOLPEADA POR LA POLICIA!!
@Getter
public class HechosData {


    private String titulo;
    private String descripcion;
    private List<ContenidoMultimedia> contenidosMultimedia;
    private String fechaAcontecimiento;
    private Long categoria_id;
    private Long ubicacion_id;
    private Double latitud;
    private Double longitud;

    public HechosData(String titulo, String descripcion, String fechaAcontecimiento, List<ContenidoMultimedia> contenidosMultimedia, Long categoria_id, Long ubicacion_id, Double latitud, Double longitud) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.contenidosMultimedia = contenidosMultimedia;
        this.categoria_id = categoria_id;
        this.ubicacion_id = ubicacion_id;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
