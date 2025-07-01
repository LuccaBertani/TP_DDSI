package modulos.shared;

import lombok.Getter;
import lombok.Setter;
import modulos.fuentes.Dataset;
import modulos.fuentes.Origen;
import modulos.shared.dtos.AtributosHecho;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Hecho {
    private Boolean activo;
    private Long id;
    private Long id_usuario;

    private AtributosHecho atributosHecho;

    private List<Dataset> datasets;

    private Pais pais;
    private String titulo;
    private String descripcion;
    private ZonedDateTime fechaAcontecimiento;
    private TipoContenido contenidoMultimedia;
    private Categoria categoria;
    private Origen origen;
    private ZonedDateTime fechaDeCarga;
    private ZonedDateTime fechaUltimaActualizacion;


    public Hecho() {
        this.id_usuario=-1L;
        this.datasets = new ArrayList<>();
        //this.atributosHecho = atributosHecho;
    }
}
