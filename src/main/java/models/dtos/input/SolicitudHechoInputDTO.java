package models.dtos.input;

import lombok.Data;



@Data
public class SolicitudHechoInputDTO { //datos del hecho y el id del usuario
    private Long id_usuario; //el que ejecuta la acción
    String titulo;
    String descripcion;
    Integer tipoContenido;
    String pais;
    String fechaAcontecimiento;
}
