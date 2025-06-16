package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetHechosColeccionInputDTO {
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

    @NotNull(message = "La forma de navegación debe ser especificada")
    private Boolean navegacionCurada;

    @NotNull(message = "El id_coleccion es obligatorio")
    private Long id_coleccion;
}
