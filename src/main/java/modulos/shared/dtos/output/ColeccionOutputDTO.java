package modulos.shared.dtos.output;

import lombok.Data;
import modulos.shared.dtos.input.CriteriosColeccionDTO;
import modulos.shared.dtos.input.CriteriosColeccionProxyDTO;
import modulos.shared.dtos.input.ProxyDTO;


import java.util.List;

@Data
public class ColeccionOutputDTO {
    private Long id;
    private String titulo;
    private String descripcion;

    private CriteriosColeccionProxyDTO criterios;
}
