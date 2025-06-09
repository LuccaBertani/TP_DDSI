package modulos.shared.dtos.output;

import lombok.Data;

@Data
public class MensajesHechosUsuarioOutputDTO {
    Long id_usuario;
     Long id_hecho;
     Long id_mensaje;
     String mensaje;
 }
