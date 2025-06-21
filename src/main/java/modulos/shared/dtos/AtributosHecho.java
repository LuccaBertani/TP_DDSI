
package modulos.shared.dtos;

import lombok.Data;
import modulos.shared.Categoria;
import modulos.fuentes.Origen;
import modulos.shared.Pais;
import modulos.shared.TipoContenido;

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
