package models.dtos.input;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class SolicitudHechoEvaluarInputDTO {
    private Boolean respuesta;
    private Long id_solicitud;
    private Long id_usuario; //el que ejecuta la acción
}



// Usuario -> solicitud (datos del hecho) -> crear instancia SolicitudHecho (usuario,hecho) -> valido = false
//Administrador -> evaluarsolicitud(id_solicitud,respuesta) -> if(respuesta) -> valido = true
