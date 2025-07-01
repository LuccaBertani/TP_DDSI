package modulos.fuentes;

import lombok.Data;

@Data
public class Dataset {
    private Long id;
    private String fuente;

    public Dataset(String fuente, Long id) {
        this.fuente = fuente;
        this.id = id;
    }
}