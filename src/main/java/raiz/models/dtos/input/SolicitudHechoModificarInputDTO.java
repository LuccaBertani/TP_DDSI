package raiz.models.dtos.input;

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
    String titulo;
    String descripcion;
    Integer tipoContenido;
    String pais;
    String fechaAcontecimiento;
}