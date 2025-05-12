package models.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModificadorHechos {

    private List<Hecho> hechosASubir;
    private List<Hecho> hechosAModificar;

    public ModificadorHechos(List<Hecho> hechosASubir, List<Hecho> hechosAModificar){
        this.hechosASubir = hechosASubir;
        this.hechosAModificar = hechosAModificar;
    }




}
