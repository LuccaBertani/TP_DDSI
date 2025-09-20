
package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Data;
import modulos.agregacion.entities.fuentes.Origen;

import java.time.ZonedDateTime;

@Data
@Embeddable
public class AtributosHecho {

    @Column(name = "titulo", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String titulo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "ubicacion_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_hecho_ubicacion"))
    private Ubicacion ubicacion;

    @Column(name = "descripcion", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci")
    private String descripcion;

    @Column(name = "fechaAcontecimiento")
    private ZonedDateTime fechaAcontecimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoContenidoMultimedia", length = 20)
    private TipoContenido contenidoMultimedia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen", length = 20)
    private Origen origen;

    @Column(name = "fechaCarga")
    private ZonedDateTime fechaCarga;

    @Column(name = "fechaUltimaActualizacion")
    private ZonedDateTime fechaUltimaActualizacion;

    //se persiste porque si se corta la luz, se tiene que saber los hechos que estan modificados y todavia no revisados
    @Column(name = "modificado")
    private Boolean modificado;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;
}