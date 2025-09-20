package modulos.agregacion.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.ZonedDateTime;


@Data
@Table(name = "atributos_modificar_hecho")
@Entity
public class AtributosHechoModificar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoContenidoMultimedia", length = 20)
    private TipoContenido contenidoMultimedia;

    @Column(name = "fechaAcontecimiento")
    private ZonedDateTime fechaAcontecimiento;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "ubicacion_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_atributosAModificar_ubicacion"))
    private Ubicacion ubicacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_categoria_hechoAModificar"))
    private Categoria categoria;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

}