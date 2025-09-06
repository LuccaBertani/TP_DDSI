package modulos.shared.dtos.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SolicitudHechoModificarInputDTO { //datos del hecho y el id del usuario
    @NotNull(message = "El id_usuario es obligatorio")
    private Long id_usuario; //el que ejecuta la acción
    @NotNull(message = "El id_hecho es obligatorio")
    private Long id_hecho; // Id del hecho que se quiere modificar
    @NotNull(message = "El titulo es obligatorio")
    private String titulo;
    @NotNull(message = "El origen es obligatorio")
    private Integer origen;

    private String descripcion;
    private Integer tipoContenido;
    private String pais;
    private String fechaAcontecimiento;
    private String provincia;
    private String categoria;
}