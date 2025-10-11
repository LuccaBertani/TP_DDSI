package modulos.Front.dtos.output;

import lombok.Data;

@Data
public class UsuarioOutputDto {
    private Long id;
    private String nombreDeUsuario;
    private String nombre;
    private String apellido;
    private Integer edad;
    private Integer cantHechosSubidos;
}
