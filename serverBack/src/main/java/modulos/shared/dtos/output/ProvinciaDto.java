package modulos.shared.dtos.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProvinciaDto {
    private String provincia;
    private Long id;
}
