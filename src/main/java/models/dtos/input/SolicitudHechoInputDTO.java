package models.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class SolicitudHechoInputDTO { //datos del hecho y el id del usuario
    @NotNull(message = "El id de usuario es obligatorio")
    private Long id_usuario; //el que ejecuta la acción

    @NotNull(message = "El titulo del hecho es obligatorio")
    String titulo;

    String descripcion;
    Integer tipoContenido;
    String pais;
    String fechaAcontecimiento;
    String categoria;
}
