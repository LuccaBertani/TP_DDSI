package modulos.agregacion.entities;

import lombok.Builder;
import lombok.Data;
import modulos.agregacion.entities.DbMain.Categoria;
import modulos.agregacion.entities.DbMain.Ubicacion;
import modulos.agregacion.entities.atributosHecho.TipoContenido;

import java.time.ZonedDateTime;

@Builder
@Data
public class AtributosHechoModificarMemoria {

    private Long id;

    private String titulo;

    private String descripcion;

    private TipoContenido contenidoMultimedia;


    private ZonedDateTime fechaAcontecimiento;

    private Ubicacion ubicacion;

    private Categoria categoria;

    private Double latitud;

    private Double longitud;
    public AtributosHechoModificarMemoria(){}
}
