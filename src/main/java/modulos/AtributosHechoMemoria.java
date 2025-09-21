package modulos;


import lombok.Builder;
import lombok.Data;
import modulos.agregacion.entities.DbMain.Ubicacion;
import modulos.agregacion.entities.DbMain.Categoria;
import modulos.agregacion.entities.atributosHecho.Origen;
import modulos.agregacion.entities.atributosHecho.TipoContenido;

import java.time.ZonedDateTime;

@Data
@Builder
public class AtributosHechoMemoria {

    private String titulo;


    private Ubicacion ubicacion;

    private String descripcion;

    private ZonedDateTime fechaAcontecimiento;


    private TipoContenido contenidoMultimedia;


    private Categoria categoria;

    private Origen origen;


    private ZonedDateTime fechaCarga;

    private ZonedDateTime fechaUltimaActualizacion;


    private Boolean modificado;


    private Double latitud;


    private Double longitud;

    public AtributosHechoMemoria(){}

}
