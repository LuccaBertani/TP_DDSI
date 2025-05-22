package models.dtos.output;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import models.entities.Categoria;
import models.entities.Origen;
import models.entities.Pais;
import models.entities.TipoContenido;

import java.time.ZonedDateTime;

@Setter
@Data
public class SolicitudHechoOutputDTO {
    Integer codigoHTTP;
}
