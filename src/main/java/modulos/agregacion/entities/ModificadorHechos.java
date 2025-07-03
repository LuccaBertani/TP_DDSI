package modulos.agregacion.entities;

import lombok.Getter;
import lombok.Setter;
import modulos.shared.Hecho;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ModificadorHechos {

    private List<Hecho> hechosASubir;
    private Set<Hecho> hechosAModificar;

    public ModificadorHechos(List<Hecho> hechosASubir, Set<Hecho> hechosAModificar){
        this.hechosASubir = hechosASubir;
        this.hechosAModificar = hechosAModificar;
    }


}
