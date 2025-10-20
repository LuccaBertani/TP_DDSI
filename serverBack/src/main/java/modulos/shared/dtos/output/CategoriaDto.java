package modulos.shared.dtos.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaDto {
    private String categoria;
    private Long id;
}
