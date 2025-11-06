package modulos.shared.dtos.output;

import lombok.Builder;
import lombok.Data;

@Data
public class PaisProvinciaDTO {
    private PaisDto paisDto;
    private ProvinciaDto provinciaDto;
}
