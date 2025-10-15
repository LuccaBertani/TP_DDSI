package modulos.shared.dtos.output;

import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.DbMain.usuario.Rol;

@Getter
@Setter
public class RolCambiadoDTO {
    String username;
    Rol rol;
    Boolean rolModificado;
}
