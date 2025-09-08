package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class SolicitudHechoInputDTO { //datos del hecho y el id del usuario
    @NotNull(message = "El id del usuario es obligatorio")
    private Long id_usuario;
    @NotNull(message = "El titulo del hecho es obligatorio")
    private String titulo;

    private String descripcion;
    private Integer tipoContenido;
    private String fechaAcontecimiento;

    private Double latitud;
    private Double longitud;

    private Long id_pais;
    private Long id_categoria;
    private Long id_provincia;
}
