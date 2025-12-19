package modulos.Front.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import modulos.Front.dtos.input.ColeccionInputDTO;

@Data
public class ColeccionInputDTO {
    @NotNull(message = "El campo titulo es obligatorio")
    private String titulo;
    @NotNull(message = "La descripción es obligatoria")
    private String descripcion;

    private CriteriosColeccionDTO criterios;

    private String algoritmoConsenso;

    public ColeccionInputDTO() {
        this.criterios = new CriteriosColeccionDTO();
    }

}
