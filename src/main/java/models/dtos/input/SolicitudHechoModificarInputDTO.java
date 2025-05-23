package models.dtos.input;

import lombok.Data;

@Data
public class SolicitudHechoModificarInputDTO { //datos del hecho y el id del usuario
    private Long id_usuario; //el que ejecuta la acción
    private Long id_hecho; // Id del hecho que se quiere modificar
    String titulo;
    String descripcion;
    Integer tipoContenido;
    String pais;
    String fechaAcontecimiento;
}