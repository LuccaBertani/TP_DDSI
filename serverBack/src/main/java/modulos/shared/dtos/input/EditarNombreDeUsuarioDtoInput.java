package modulos.shared.dtos.input;

import lombok.Data;

@Data
public class EditarNombreDeUsuarioDtoInput {
    private String nombreDeUsuarioNuevo;
    private String contrasenia;
    private Long id;
}
