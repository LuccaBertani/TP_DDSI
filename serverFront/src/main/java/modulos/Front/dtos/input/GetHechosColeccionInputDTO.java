package modulos.Front.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetHechosColeccionInputDTO {
    private String fuente;
    private Long categoriaId;
    private Integer contenidoMultimedia;
    private String descripcion;
    private String fechaAcontecimientoInicial;
    private String fechaAcontecimientoFinal;
    private String fechaCargaInicial;
    private String fechaCargaFinal;
    private Integer origen;
    private Long paisId;
    private String titulo;
    private Long provinciaId;


    @NotNull(message = "La forma de navegación debe ser especificada")
    private Boolean navegacionCurada;

    @NotNull(message = "El id_coleccion es obligatorio")
    private Long id_coleccion;
}
