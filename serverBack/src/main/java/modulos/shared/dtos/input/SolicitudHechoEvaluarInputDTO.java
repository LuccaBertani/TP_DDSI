package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudHechoEvaluarInputDTO {
    @NotNull(message = "Debe seleccionar una opción")
    private Boolean respuesta;
    @NotNull(message = "El id_solicitud es obligatorio")
    private Long id_solicitud;

    private String mensaje;
}