
package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Data;
import modulos.agregacion.entities.fuentes.Origen;

import java.time.ZonedDateTime;

@Data
@Embeddable
public class AtributosHecho {

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ubicacion_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_usuario_hecho"))
    private Ubicacion ubicacion;

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