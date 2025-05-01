package models.entities.filtros;

import models.entities.Hecho;
import models.entities.Pais;

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
