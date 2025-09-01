package modulos.shared;

import lombok.Getter;
import modulos.agregacion.entities.Provincia;

@Getter
public class HechosData {


    String titulo;
    String descripcion;
    Integer tipoContenido;
    Pais pais;
    String fechaAcontecimiento;
    Provincia provincia;

    public HechosData(String titulo, String descripcion, Integer tipoContenido, Pais pais, String fechaAcontecimiento, Provincia provincia) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoContenido = tipoContenido;
        this.pais = pais;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.provincia = provincia;
    }

}
