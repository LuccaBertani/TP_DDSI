package models.dtos.input;


import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class VisualizarHechosInputDTO {
    long id_usuario;
    long id_coleccion;
}
