package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
public class ColeccionInputDTO {
    @NotNull(message = "El id_usuario es obligatorio")
    private Long id_usuario;
    @NotNull(message = "El campo titulo es obligatorio")
    private String titulo;
    @NotNull(message = "La descripción es obligatoria")
    private String descripcion;

    //Criterios (es decir los filtros)
    private String categoria;
    private Integer contenidoMultimedia;
    private String fechaAcontecimientoInicial;
    private String fechaAcontecimientoFinal;
    private String fechaCargaInicial;
    private String fechaCargaFinal;
    private Integer origen;
    private String pais;
    private String descripcionFiltro;
    private String tituloFiltro;

    // Algoritmo de consenso (opcional)
    private String algoritmoConsenso;
}
