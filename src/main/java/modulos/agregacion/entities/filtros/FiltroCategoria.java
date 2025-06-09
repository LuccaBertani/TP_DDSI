package modulos.agregacion.entities.filtros;

import modulos.shared.Categoria;
import modulos.shared.Hecho;


public class FiltroCategoria implements Filtro {

    private Categoria categoria;

    public FiltroCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria(){
        return this.categoria;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getCategoria() == this.categoria; // Busca si las dos variables apuntan al mismo objeto
    }

}
