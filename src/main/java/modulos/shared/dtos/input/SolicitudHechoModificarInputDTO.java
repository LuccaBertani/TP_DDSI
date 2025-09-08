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
    private String titulo;
    private String descripcion;
    private Integer tipoContenido;
    private String fechaAcontecimiento;
    private Double latitud;
    private Double longitud;
    private Long id_provincia;
    private Long id_categoria;
    private Long id_pais;
}