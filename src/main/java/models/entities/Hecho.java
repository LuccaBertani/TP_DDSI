package models.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class Hecho {
    private Boolean activo;
    private Long id;
    private Long id_usuario;
    private String titulo;
    private String descripcion;
    private Categoria categoria;
    private TipoContenido contenidoMultimedia;
    private Pais pais;
    private ZonedDateTime fechaAcontecimiento;
    private ZonedDateTime fechaDeCarga;
    private Origen origen;
}
