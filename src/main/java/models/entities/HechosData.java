package models.entities;

import lombok.Getter;

@Getter
public class HechosData {


    String titulo;
    String descripcion;
    Integer tipoContenido;
    Pais pais;
    String fechaAcontecimiento;
    Long id;

    public HechosData(String titulo, String descripcion, Integer tipoContenido, Pais pais, String fechaAcontecimiento, Long id) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoContenido = tipoContenido;
        this.pais = pais;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.id = id;
    }

}
