package modulos.shared.dtos.input;

import lombok.Data;

@Data
public class CriteriosColeccionDTO {
    private Long categoriaId;
    private Integer contenidoMultimedia;
    private String descripcion;
    private String fechaAcontecimientoInicial;
    private String fechaAcontecimientoFinal;
    private String fechaCargaInicial;
    private String fechaCargaFinal;
    private Integer origen;
    private Long paisId;
    private Long provinciaId;
    private String titulo;

    public CriteriosColeccionDTO(){
    }

    public CriteriosColeccionDTO(Long categoriaId, Integer contenidoMultimedia, String descripcion, String fechaAcontecimientoInicial, String fechaAcontecimientoFinal, String fechaCargaInicial, String fechaCargaFinal, Integer origen, Long paisId, String titulo, Long provinciaId) {
        this.categoriaId = categoriaId;
        this.contenidoMultimedia = contenidoMultimedia;
        this.descripcion = descripcion;
        this.fechaAcontecimientoInicial = fechaAcontecimientoInicial;
        this.fechaAcontecimientoFinal = fechaAcontecimientoFinal;
        this.fechaCargaInicial = fechaCargaInicial;
        this.fechaCargaFinal = fechaCargaFinal;
        this.origen = origen;
        this.paisId = paisId;
        this.titulo = titulo;
        this.provinciaId = provinciaId;
    }
}
