package modulos.shared.dtos.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MensajeOutputDTO {
     private Long id_usuario;
     private Long id_solicitud_hecho;
     private Long id_mensaje;
     private String mensaje;
 }
