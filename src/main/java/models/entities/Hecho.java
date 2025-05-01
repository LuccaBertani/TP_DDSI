package models.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class Hecho {
    private Boolean activo;
    private Long id;
    private String titulo;
    private String descripcion;
    private Categoria categoria;
    private TipoContenido contenidoMultimediaOpcional;
    private Pais pais;
    private ZonedDateTime fechaAcontecimiento;
    private ZonedDateTime fechaDeCarga;
    private Origen origen;
}
