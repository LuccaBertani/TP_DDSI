package modulos.agregacion.entities;

import lombok.Getter;

@Getter
public class HechosData {


    String titulo;
    String descripcion;
    Integer tipoContenido;
    String fechaAcontecimiento;
    Categoria categoria;
    Ubicacion ubicacion;
    Double latitud;
    Double longitud;

    public HechosData(String titulo, String descripcion, Integer tipoContenido, String fechaAcontecimiento, Categoria categoria, Ubicacion ubicacion, Double latitud, Double longitud) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoContenido = tipoContenido;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
