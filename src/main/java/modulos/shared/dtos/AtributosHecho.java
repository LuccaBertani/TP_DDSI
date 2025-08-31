
package modulos.shared.dtos;

import jakarta.persistence.*;
import lombok.Data;
import modulos.shared.Categoria;
import modulos.fuentes.Origen;
import modulos.shared.Pais;
import modulos.shared.TipoContenido;

import java.time.ZonedDateTime;

@Data
@Embeddable
public class AtributosHecho {

    //FK a la tabla Pais relacion n a 1
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pais_id") // FK va en la tabla de HECHO
    private Pais pais;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fechaAcontecimiento")
    private ZonedDateTime fechaAcontecimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoContenidoMultimedia", nullable = false, length = 20)
    private TipoContenido contenidoMultimedia;

    //FK a la tabla Categoria relacion n a 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen",nullable = false, length = 20)
    private Origen origen;

    @Column(name = "fechaCarga", nullable = false)
    private ZonedDateTime fechaCarga;

    @Column(name = "fechaUltimaActualizacion")
    private ZonedDateTime fechaUltimaActualizacion;

    //se persiste porque si se corta la luz, se tiene que saber los hechos que estan modificados y todavia no revisados
    @Column(name = "modificado")
    private Boolean modificado;
}