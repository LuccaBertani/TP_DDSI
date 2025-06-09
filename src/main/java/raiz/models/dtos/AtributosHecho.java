package raiz.models.dtos;

import lombok.Data;
import raiz.models.entities.Categoria;
import raiz.models.entities.Origen;
import raiz.models.entities.Pais;
import raiz.models.entities.TipoContenido;

import java.time.ZonedDateTime;

@Data
public class AtributosHecho {
    Pais pais;
    String titulo;
    String descripcion;
    ZonedDateTime fechaAcontecimiento;
    TipoContenido contenidoMultimedia;
    Categoria categoria;
    Origen origen;
    ZonedDateTime fechaCarga;
    ZonedDateTime fechaUltimaActualizacion;
}
