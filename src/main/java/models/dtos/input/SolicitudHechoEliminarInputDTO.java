package models.dtos.input;

import lombok.Data;
import lombok.Getter;


@Data
public class SolicitudHechoEliminarInputDTO {
    Long id_usuario;
    Long id_hecho;
}
