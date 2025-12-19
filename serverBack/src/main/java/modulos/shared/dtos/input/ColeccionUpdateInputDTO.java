package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ColeccionUpdateInputDTO {
    @NotNull(message = "el id_coleccion es obligatorio")
    private Long id_coleccion;

    private String titulo;
    private String descripcion;

    private CriteriosColeccionDTO criterios;

    private String algoritmoConsenso;
}
