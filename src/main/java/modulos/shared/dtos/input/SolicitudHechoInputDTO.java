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
    private String pais;
    private String fechaAcontecimiento;
    private String categoria;
    private String provincia;
}
