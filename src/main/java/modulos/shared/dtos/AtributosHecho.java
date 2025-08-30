
package modulos.shared.dtos;

import lombok.Data;
import modulos.shared.Categoria;
import modulos.fuentes.Origen;
import modulos.shared.Pais;
import modulos.shared.TipoContenido;

import java.time.ZonedDateTime;

@Data
public class AtributosHecho {
    private Pais pais;
    private String titulo;
    private String descripcion;
    private ZonedDateTime fechaAcontecimiento;
    private TipoContenido contenidoMultimedia;
    private Categoria categoria;
    private Origen origen;
    private ZonedDateTime fechaCarga;
    private ZonedDateTime fechaUltimaActualizacion;
    private Boolean modificado;
}