package models.dtos.input;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import models.entities.Categoria;
import models.entities.Origen;
import models.entities.Pais;
import models.entities.TipoContenido;

import java.time.ZonedDateTime;

@Getter
@Data
public class SolicitudHechoInputDTO { //datos del hecho y el id del usuario
    private Long id_usuario; //el que ejecuta la acción
    String titulo;
    String descripcion;
    Integer tipoContenido;
    String pais;
    String fechaAcontecimiento;
}
