package modulos.agregacion.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaisProvincias {
    private Pais pais;
    private List<Provincia> provincias;

    public PaisProvincias(Pais pais, List<Provincia> provincias) {
        this.pais = pais;
        this.provincias = provincias;
    }

    public PaisProvincias(){
        this.provincias = new ArrayList<>();
    }
}
