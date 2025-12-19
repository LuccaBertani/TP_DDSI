package modulos.Front.dtos.input;

import lombok.Data;

@Data
public class SinonimoInputDTO {
    private String tipo;

    private Long id_entidad;

    private Long id_pais;

    private String sinonimo;
}