package raiz.models.dtos.input;

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
    private CriteriosColeccionDTO criterios;

    // Algoritmo de consenso (opcional)
    private String algoritmoConsenso;
}
