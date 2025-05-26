package raiz.models.entities.filtros;

import raiz.models.entities.Hecho;
import raiz.models.entities.Pais;

public class FiltroPais implements Filtro {

private Pais pais;

    public FiltroPais(Pais pais) {
        this.pais = pais;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getPais() == pais;
    }
}
