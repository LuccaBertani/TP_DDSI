package modulos.agregacion.entities.atributosHecho;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;


@Data
@Table(name = "atributos_modificar_hecho")
@Entity
@Builder
@AllArgsConstructor
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

    @Column(name = "ubicacion_id")
    private Long ubicacion_id;

    @Column(name = "categoria_id")
    private Long categoria;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    public AtributosHechoModificar() {

    }
}