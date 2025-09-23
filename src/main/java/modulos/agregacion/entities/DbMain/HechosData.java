package modulos.agregacion.entities.DbMain;

import lombok.Getter;

@Getter
public class HechosData {


    String titulo;
    String descripcion;
    Integer tipoContenido;
    String fechaAcontecimiento;
    Long categoria_id;
    Long ubicacion_id;
    Double latitud;
    Double longitud;

    public HechosData(String titulo, String descripcion, String fechaAcontecimiento, Integer tipoContenido, Long categoria_id, Long ubicacion_id, Double latitud, Double longitud) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.tipoContenido = tipoContenido;
        this.categoria_id = categoria_id;
        this.ubicacion_id = ubicacion_id;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
