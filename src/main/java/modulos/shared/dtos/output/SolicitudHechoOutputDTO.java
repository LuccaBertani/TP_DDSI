package modulos.shared.dtos.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolicitudHechoOutputDTO {
    private Long id;
    private Long usuarioId;
    private Long hechoId;
    private String justificacion;
    private Boolean procesada;
    private Boolean rechazadaPorSpam;
}
