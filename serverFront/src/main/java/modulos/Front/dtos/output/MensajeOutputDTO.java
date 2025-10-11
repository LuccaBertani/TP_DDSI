package modulos.Front.dtos.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MensajeOutputDTO {
     Long id_usuario;
     Long id_solicitud_hecho;
     Long id_mensaje;
     String mensaje;
 }
