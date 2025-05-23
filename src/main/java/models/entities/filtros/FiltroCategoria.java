package models.entities.filtros;

import models.entities.Categoria;
import models.entities.Hecho;

public class FiltroCategoria implements Filtro {

    Categoria categoria;

    public FiltroCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getCategoria() == this.categoria; // Busca si las dos variables apuntan al mismo objeto
    }

}
