package modulos.shared.dtos.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModificarConsensoInputDTO {
    private Long idColeccion;
    private String tipoConsenso; //multiples menciones, mayría simple, mayoría absoluta
    private Long idUsuario;
}
