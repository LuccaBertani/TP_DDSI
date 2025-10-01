package modulos;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import modulos.agregacion.entities.DbMain.Ubicacion;
import modulos.agregacion.entities.DbMain.Categoria;
import modulos.agregacion.entities.atributosHecho.ContenidoMultimedia;
import modulos.agregacion.entities.atributosHecho.Origen;
import modulos.agregacion.entities.atributosHecho.TipoContenido;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AtributosHechoMemoria {

    private String titulo;

    private Ubicacion ubicacion;

    private String descripcion;

    private ZonedDateTime fechaAcontecimiento;

    private List<ContenidoMultimedia> contenidoMultimedia;

    private Categoria categoria;

    private Origen origen;

    private ZonedDateTime fechaCarga;

    private ZonedDateTime fechaUltimaActualizacion;

    private Boolean modificado;

    private Double latitud;

    private Double longitud;

    public AtributosHechoMemoria(){}

}
