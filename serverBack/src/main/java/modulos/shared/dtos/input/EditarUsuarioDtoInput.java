package modulos.shared.dtos.input;

import lombok.Data;

@Data
public class EditarUsuarioDtoInput {
    private Long id;
    private String nombre;
    private String apellido;
    private Integer edad;
}
