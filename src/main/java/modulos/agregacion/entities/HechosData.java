package modulos.agregacion.entities;

import lombok.Getter;

@Getter
public class HechosData {


    String titulo;
    String descripcion;
    Integer tipoContenido;
    Pais pais;
    String fechaAcontecimiento;
    Provincia provincia;
    Categoria categoria;

    public HechosData(String titulo, String descripcion, Integer tipoContenido, Pais pais, String fechaAcontecimiento, Provincia provincia, Categoria categoria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoContenido = tipoContenido;
        this.pais = pais;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.provincia = provincia;
        this.categoria = categoria;
    }

}
