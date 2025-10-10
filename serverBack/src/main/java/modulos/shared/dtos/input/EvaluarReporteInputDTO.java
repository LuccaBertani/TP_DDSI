package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluarReporteInputDTO {
    @NotNull(message = "El usuario debe indicarse")
    private Long usuario_id;

    @NotNull(message = "El reporte debe indicarse")
    private Long reporte_id;

    @NotNull(message = "Debe indicarse una rta")
    private Boolean respuesta;
}
