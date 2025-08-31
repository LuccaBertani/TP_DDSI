package modulos.shared;

import lombok.Getter;

@Getter
public class HechosData {


    String titulo;
    String descripcion;
    Integer tipoContenido;
    Pais pais;
    String fechaAcontecimiento;


    public HechosData(String titulo, String descripcion, Integer tipoContenido, Pais pais, String fechaAcontecimiento) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoContenido = tipoContenido;
        this.pais = pais;
        this.fechaAcontecimiento = fechaAcontecimiento;
    }

}
