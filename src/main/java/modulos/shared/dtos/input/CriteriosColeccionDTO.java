package modulos.shared.dtos.input;

import lombok.Data;

@Data
public class CriteriosColeccionDTO {
    private String categoria;
    private String contenidoMultimedia;
    private String descripcion;
    private String fechaAcontecimientoInicial;
    private String fechaAcontecimientoFinal;
    private String fechaCargaInicial;
    private String fechaCargaFinal;
    private String origen;
    private String pais;
    private String titulo;

    public CriteriosColeccionDTO(){
    }

    public CriteriosColeccionDTO(String categoria, String contenidoMultimedia, String descripcion, String fechaAcontecimientoInicial, String fechaAcontecimientoFinal, String fechaCargaInicial, String fechaCargaFinal, String origen, String pais, String titulo) {
        this.categoria = categoria;
        this.contenidoMultimedia = contenidoMultimedia;
        this.descripcion = descripcion;
        this.fechaAcontecimientoInicial = fechaAcontecimientoInicial;
        this.fechaAcontecimientoFinal = fechaAcontecimientoFinal;
        this.fechaCargaInicial = fechaCargaInicial;
        this.fechaCargaFinal = fechaCargaFinal;
        this.origen = origen;
        this.pais = pais;
        this.titulo = titulo;
    }
}
